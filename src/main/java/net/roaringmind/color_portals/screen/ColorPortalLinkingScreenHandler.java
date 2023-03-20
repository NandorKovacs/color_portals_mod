package net.roaringmind.color_portals.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.roaringmind.color_portals.ColorPortals;

public class ColorPortalLinkingScreenHandler extends ScreenHandler {
  private int cost;

  public ColorPortalLinkingScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
    this(syncId, playerInventory, 0);
    cost = buf.readInt();
  }

  public ColorPortalLinkingScreenHandler(int syncId, PlayerInventory playerInventory, int cost) {
    super(ColorPortals.COLOR_PORTAL_LINKING_SCREEN_HANDLER, syncId);
    this.cost = 0;

    int m;
    int l;
    // The player inventory
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
      }
    }
    // The player Hotbar
    for (m = 0; m < 9; ++m) {
      this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
    }
  }

  public int getCost() {
    return cost;
  }
  
  @Override
  public ItemStack quickMove(PlayerEntity var1, int var2) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean canUse(PlayerEntity var1) {
    return true;
  }

}
