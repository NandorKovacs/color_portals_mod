package net.roaringmind.color_portals.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.screen.ColorPortalScreenHandler;

public class ColorPortalBaseEntity extends BlockEntity implements NamedScreenHandlerFactory {

  public ColorPortalBaseEntity(BlockPos pos, BlockState state) {
    super(ColorPortals.COLOR_PORTAL_BASE_ENTITY, pos, state);
  }

  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
    return new ColorPortalScreenHandler(syncId, playerInventory, playerInventory);
  }

  @Override
  public Text getDisplayName() {
    return Text.translatable(getCachedState().getBlock().getTranslationKey());
  }
  
  
}
