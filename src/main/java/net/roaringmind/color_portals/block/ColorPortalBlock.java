package net.roaringmind.color_portals.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.roaringmind.color_portals.ColorPortal;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;

public class ColorPortalBlock extends BlockWithEntity {

  public ColorPortalBlock(Settings settings) {
    super(settings);
  }

  // copied from NetherPortalBlock {
  public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
  protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
  protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

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
    builder.add(AXIS);
  }

  public BlockState getStateWithRotation(Direction.Axis axis) {
    return this.getDefaultState().with(AXIS, axis);
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
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
      if (world instanceof ServerWorld && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
          // fix reg key
          RegistryKey<World> registryKey = ColorPortal.getById(((ColorPortalBlockEntity) world.getBlockEntity(pos)).getPortal()).getDimension();
          ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(registryKey);
          if (serverWorld == null) {
              return;
          }
          entity.moveToWorld(serverWorld);
      }
  }
}
