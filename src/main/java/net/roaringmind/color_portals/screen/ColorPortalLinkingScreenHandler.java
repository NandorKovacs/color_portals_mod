package net.roaringmind.color_portals.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.roaringmind.color_portals.ColorPortals;

public class ColorPortalLinkingScreenHandler extends ScreenHandler {
  private int cost;
  private GlobalPos pos;
  public ScreenHandlerContext context = ScreenHandlerContext.EMPTY;

  public ColorPortalLinkingScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
    this(syncId, playerInventory, 0, GlobalPos.create(World.OVERWORLD, BlockPos.ORIGIN), ScreenHandlerContext.EMPTY);
    cost = buf.readInt();
    pos = buf.readGlobalPos();
  }

  public ColorPortalLinkingScreenHandler(int syncId, PlayerInventory playerInventory, int cost, GlobalPos pos, ScreenHandlerContext context) {
    super(ColorPortals.COLOR_PORTAL_LINKING_SCREEN_HANDLER, syncId);
    this.cost = 0;
    this.pos = GlobalPos.create(World.OVERWORLD, BlockPos.ORIGIN);
    this.context = context;

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
  
  public GlobalPos getPos() {
    return pos;
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
