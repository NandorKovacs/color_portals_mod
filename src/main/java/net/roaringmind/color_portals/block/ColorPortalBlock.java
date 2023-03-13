package net.roaringmind.color_portals.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ColorPortalBlock extends Block {

  public ColorPortalBlock(Settings settings) {
    super(settings);
  }

  // copied from NetherPortalBlock {
  public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;
  protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
  protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    switch (state.get(AXIS)) {
      case Z: {
        return Z_SHAPE;
      }
    }
    return X_SHAPE;
  }

  @Override
  public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
    return ItemStack.EMPTY;
  }

  @Override
  public BlockState rotate(BlockState state, BlockRotation rotation) {
    switch (rotation) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90: {
        switch (state.get(AXIS)) {
          case X: {
            return (BlockState) state.with(AXIS, Direction.Axis.Z);
          }
          case Z: {
            return (BlockState) state.with(AXIS, Direction.Axis.X);
          }
        }
        return state;
      }
    }
    return state;
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
      builder.add(AXIS);
  }
  // }

  public BlockState getStateWithRotation(Direction.Axis axis) {
    return this.getDefaultState().with(AXIS, axis);
  }
}
