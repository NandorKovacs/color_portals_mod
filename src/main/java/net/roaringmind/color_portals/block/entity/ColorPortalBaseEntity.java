package net.roaringmind.color_portals.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;
import net.roaringmind.color_portals.screen.ColorPortalLinkingScreenHandler;

public class ColorPortalBaseEntity extends BlockEntity implements NamedScreenHandlerFactory {
  private int portal_id = -1;

  public ColorPortalBaseEntity(BlockPos pos, BlockState state) {
    super(ColorPortals.COLOR_PORTAL_BASE_ENTITY, pos, state);
  }

  @Override
  public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
    if (this.world.getBlockState(this.pos).get(ColorPortalBase.COLOR).getId() > 15) {
      return new ColorPortalActivationScreenHandler(syncId, playerInventory, new SimpleInventory(1),
        ScreenHandlerContext.create(this.world, this.pos));
    }

    return new ColorPortalLinkingScreenHandler(syncId, playerInventory);
  }

  @Override
  public Text getDisplayName() {
    return Text.translatable(getCachedState().getBlock().getTranslationKey());
  }

  public void setPortal(int portal) {
    this.portal_id = portal;
  }

  @Override
  public void writeNbt(NbtCompound nbt) {
    nbt.putInt("color_portals_portal", portal_id);

    super.writeNbt(nbt);
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);

    portal_id = nbt.getInt("color_portals_portal");
  }
}
