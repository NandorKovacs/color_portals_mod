package net.roaringmind.color_portals.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColorPortalBaseBlock extends BlockWithEntity {
  private static final Text TITLE = Text.of("pooortal");

  public ColorPortalBaseBlock(AbstractBlock.Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      if (world.isClient) {
          return ActionResult.SUCCESS;
      }
      player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
      return ActionResult.CONSUME;
  }

  @Override
  public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
      return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)), TITLE);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos var1, BlockState var2) {
    return n
  }
}
