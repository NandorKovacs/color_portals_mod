package net.roaringmind.color_portals.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.roaringmind.color_portals.ColorPortal;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;
import net.roaringmind.color_portals.block.enums.BaseColor;

public class ColorPortalBlock extends BlockWithEntity {

  public ColorPortalBlock(Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Axis.X).with(COLOR, BaseColor.WHITE));
  }

  // copied from NetherPortalBlock {
  public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
  protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
  protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
  public static final EnumProperty<BaseColor> COLOR = EnumProperty.of("color_portal_block_color", BaseColor.class);

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    if (state.get(AXIS) == Axis.Z) {
      return Z_SHAPE;
    }
    return X_SHAPE;
  }

  @Override
  public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
    return ItemStack.EMPTY;
  }

  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    if (rotation == BlockRotation.COUNTERCLOCKWISE_90 || rotation == BlockRotation.CLOCKWISE_90) {
      if (state.get(AXIS) == Axis.X) {
        return (BlockState) state.with(AXIS, Direction.Axis.Z);
      }
      if (state.get(AXIS) == Axis.Z) {
        return (BlockState) state.with(AXIS, Direction.Axis.X);
      }
      return state;
    }
    return state;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(AXIS, COLOR);
  }

  public BlockState getColoredStateWithRotation(Direction.Axis axis, BaseColor color) {
    return this.getDefaultState().with(AXIS, axis).with(COLOR, color);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos var1, BlockState var2) {
    return new ColorPortalBlockEntity(var1, var2);
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
      WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    boolean correct_axis = !(direction.getAxis() != state.get(AXIS) && direction.getAxis().isHorizontal());

    if (!correct_axis || neighborState.isOf(this)) {
      return state;
    }

    ColorPortal portal = ColorPortal.getById(((ColorPortalBlockEntity) world.getBlockEntity(pos)).getPortal());
    if (portal != null) {
      portal.destroy(world);
    }

    return Blocks.AIR.getDefaultState();
  }

  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    ColorPortal portal = ColorPortal.getById(((ColorPortalBlockEntity) world.getBlockEntity(pos)).getPortal());
    if (portal != null) {
      portal.destroy(world);
    }

    if (newState.isAir() && ((ColorPortalBlockEntity) world.getBlockEntity(pos)).isBase()) {
      world.setBlockState(pos, ColorPortals.COLOR_PORTAL_BASE.getDefaultState().with(ColorPortalBase.FACING,
          Direction.from(state.get(AXIS) == Axis.X ? Axis.Z : Axis.X, Direction.AxisDirection.POSITIVE)));
    }
  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos,
      Entity entity) {
    if (world instanceof ServerWorld && !entity.hasVehicle() &&
        !entity.hasPassengers() && entity.canUsePortals()) {
      // fix reg key
      int portal_id = ((ColorPortalBlockEntity) world.getBlockEntity(pos)).getPortal();
      RegistryKey<World> registryKey = ColorPortals.portalRegistry.getPartnerDimension(portal_id);
      ServerWorld serverWorld = ((ServerWorld) world).getServer().getWorld(registryKey);
      if (serverWorld == null) {
        return;
      }
      moveToWorld(entity, serverWorld, world, portal_id);
    }
  }

  // !this is very bad
  // it is copied code from the source
  // it should asap be rewritten to be a mixin
  // for now it should work though
  public Entity moveToWorld(Entity e, ServerWorld destination, World startingWorld, int portal_id) {
      if (!(e.world instanceof ServerWorld) || e.isRemoved()) {
          return null;
      }
      e.world.getProfiler().push("changeDimension");
      e.detach();
      e.world.getProfiler().push("reposition");
      TeleportTarget teleportTarget = ColorPortals.portalRegistry.getTeleportTarget(portal_id, startingWorld, destination, e.getYaw(), e.getPitch(), e.getVelocity());
      if (teleportTarget == null) {
          return null;
      }
      e.world.getProfiler().swap("reloading");
      Entity entity = e.getType().create(destination);
      if (entity != null) {
          ((Entity)entity).copyFrom(e);
          ((Entity)entity).refreshPositionAndAngles(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z, teleportTarget.yaw, ((Entity)entity).getPitch());
          ((Entity)entity).setVelocity(teleportTarget.velocity);
          destination.onDimensionChanged((Entity)entity);
          if (destination.getRegistryKey() == World.END) {
              ServerWorld.createEndSpawnPlatform(destination);
          }
      }
      e.setRemoved(RemovalReason.CHANGED_DIMENSION);
      if (e instanceof MobEntity) {
        ((MobEntity)e).detachLeash(true, false);
        ((MobEntity)e).getItemsEquipped().forEach(stack -> stack.setCount(0));
      }
      e.world.getProfiler().pop();
      ((ServerWorld)e.world).resetIdleTimeout();
      destination.resetIdleTimeout();
      e.world.getProfiler().pop();
      return entity;
  }
}
