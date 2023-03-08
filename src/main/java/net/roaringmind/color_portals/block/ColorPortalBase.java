package net.roaringmind.color_portals.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;

public class ColorPortalBase extends BlockWithEntity {
  public ColorPortalBase(AbstractBlock.Settings settings) {
    super(settings);
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new ColorPortalBaseEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need
    // to change that!
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
}
