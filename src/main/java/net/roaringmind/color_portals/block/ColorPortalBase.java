package net.roaringmind.color_portals.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.block.enums.BaseColor;

public class ColorPortalBase extends BlockWithEntity {
  public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
  public static final EnumProperty<BaseColor> COLOR = EnumProperty.of("color_portal_base_color", BaseColor.class);

  public ColorPortalBase(AbstractBlock.Settings settings) {
    super(settings);
    this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(COLOR, BaseColor.NONE));
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ColorPortalBaseEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
      BlockHitResult hit) {
    if (!world.isClient) {
      // This will call the createScreenHandlerFactory method from BlockWithEntity,
      // which will return our blockEntity casted to
      // a namedScreenHandlerFactory. If your block class does not extend
      // BlockWithEntity, it needs to implement createScreenHandlerFactory.
      NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

      if (screenHandlerFactory != null) {
        // With this call the server will request the client to open the appropriate
        // Screenhandler
        player.openHandledScreen(screenHandlerFactory);
      }
    }
    return ActionResult.SUCCESS;
  }

  // copied from horizontal facing block
  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    return (BlockState) state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Override
  public BlockState mirror(BlockState state, BlockMirror mirror) {
    return state.rotate(mirror.getRotation(state.get(FACING)));
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, COLOR);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
  }

  public static void setColor(World world, BlockPos pos, BaseColor color) {
    BlockState state = world.getBlockState(pos);
    state = state.with(COLOR, color);
    world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
  }

  private static boolean isBorderBlock(Block block) {
    return block == Blocks.OBSIDIAN;
  }

  private static BlockPos getBlockPosAtRelativeXY(int x, int y, BlockPos pos, Direction.Axis axis) {
    switch (axis) {
      case Z:
        return new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ());
      case X:
        return new BlockPos(pos.getX(), pos.getY() + y, pos.getZ() + x);
      default:
        ColorPortals.LOGGER.warn("couldn't get block pos at relative xy " + x + " " + y + " with origin "
            + pos.toShortString() + ": axis equals Y");
        return null;
    }
  }

  private static Boolean dfs(boolean[][] visited, List<BlockPos> fillable_blocks, int x, int y, World world,
      BlockPos pos, Direction direction) {
    if (x > 10 || x < -10 || y > 10 || y < -10) {
      return false;
    }
    if (visited[x + 11][y + 11]) {
      return true;
    }
    visited[x + 11][y + 11] = true;

    BlockPos new_pos = getBlockPosAtRelativeXY(x, y, pos, direction.getAxis());
    Block block = world.getBlockState(new_pos).getBlock();

    if (isBorderBlock(block)) {
      return true;
    }

    if (!(block instanceof AirBlock)) {
      return false;
    }

    int[][] neighbors = {
        { x - 1, y },
        { x + 1, y },
        { x, y + 1 },
        { x, y - 1 }
    };

    for (var neighbor : neighbors) {
      if (!dfs(visited, fillable_blocks, neighbor[0], neighbor[1], world, pos, direction)) {
        return false;
      }
    }

    fillable_blocks.add(new_pos);
    return true;
  }

  public static Boolean setPortalBlocks(World world, BlockPos pos) {
    boolean[][] visited = new boolean[21][21];
    List<BlockPos> fillable_blocks = new ArrayList<>();

    int x = 0;
    int y = 0;
    visited[x + 11][x + 11] = true;

    int[][] neighbors = {
        { x - 1, y },
        { x + 1, y },
        { x, y + 1 },
        { x, y - 1 }
    };

    Direction direction = world.getBlockState(pos).get(FACING);

    boolean is_possible = false;
    for (int[] neighbor : neighbors) {
      if (dfs(visited, fillable_blocks, neighbor[0], neighbor[1], world, pos, direction)) {
        is_possible = true;
      }
    }

    if (is_possible) {
      for (BlockPos block_pos : fillable_blocks) {
        world.setBlockState(block_pos,
            ColorPortals.COLOR_PORTAL_BLOCK.getStateWithRotation(direction.getAxis() == Axis.X ? Axis.Z : Axis.X));
      }
      return true;
    }
    return false;
  }

  public static boolean createPortal(World world, BlockPos pos, BaseColor color) {
    if (setPortalBlocks(world, pos)) {
      setColor(world, pos, color);
      return true;
    }
    return false;
  }
}
