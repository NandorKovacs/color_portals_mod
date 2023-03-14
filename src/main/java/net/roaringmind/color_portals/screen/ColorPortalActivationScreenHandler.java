package net.roaringmind.color_portals.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.roaringmind.color_portals.ColorPortals;

public class ColorPortalActivationScreenHandler extends ScreenHandler {
  private Inventory inv = new SimpleInventory(1);
  private Slot slot = new Slot(inv, EMPTY_SPACE_SLOT_INDEX, EMPTY_SPACE_SLOT_INDEX, EMPTY_SPACE_SLOT_INDEX);

  // This constructor gets called on the client when the server wants it to open
  // the screenHandler,
  // The client will call the other constructor with an empty Inventory and the
  // screenHandler will automatically
  // sync this empty inventory with the inventory on the server.
  public ColorPortalActivationScreenHandler(int syncId, PlayerInventory playerInventory) {
    this(syncId, playerInventory, new SimpleInventory(1));
  }

  // This constructor gets called from the BlockEntity on the server without
  // calling the other constructor first, the server knows the inventory of the
  // container
  // and can therefore directly provide it as an argument. This inventory will
  // then be synced to the client.
  public ColorPortalActivationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(ColorPortals.COLOR_PORTAL_SCREEN_HANDLER, syncId);
    checkSize(inventory, 1);
    this.inv = inventory;

    int m;
    int l;

    slot = this.addSlot(new Slot(this.inv, 0, 62 + 1 * 18, 17 + 1 * 18){
      @Override 
      public int getMaxItemCount() {
        return 1;
      }
    
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.getItem() instanceof DyeItem;
      }
    });

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

  @Override
  public ItemStack quickMove(PlayerEntity var1, int var2) {
    return ItemStack.EMPTY;
  }

  public Slot getSlot() {
    return this.slot;
  }

  @Override
  public boolean canUse(PlayerEntity var1) {
    return true;
  }

}
