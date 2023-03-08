package net.roaringmind.color_portals.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.roaringmind.color_portals.ColorPortals;

public class ColorPortalScreenHandler extends ScreenHandler {

  // This constructor gets called on the client when the server wants it to open
  // the screenHandler,
  // The client will call the other constructor with an empty Inventory and the
  // screenHandler will automatically
  // sync this empty inventory with the inventory on the server.
  public ColorPortalScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(9));
  }

  // This constructor gets called from the BlockEntity on the server without
  // calling the other constructor first, the server knows the inventory of the
  // container
  // and can therefore directly provide it as an argument. This inventory will
  // then be synced to the client.
  public ColorPortalScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(ColorPortals.COLOR_PORTAL_SCREEN_HANDLER, syncId);
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
