package net.roaringmind.color_portals.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.roaringmind.color_portals.ColorPortal;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;
import net.roaringmind.color_portals.screen.ColorPortalLinkingScreenHandler;

public class ColorPortalBaseEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
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

    return new ColorPortalLinkingScreenHandler(syncId, playerInventory, 7,
        GlobalPos.create(this.world.getRegistryKey(), this.pos),
        ScreenHandlerContext.create(this.world, this.pos));
  }

  @Override
  public Text getDisplayName() {
    return Text.translatable(getCachedState().getBlock().getTranslationKey());
  }

  public void setPortalId(int portal) {
    this.portal_id = portal;
  }

  public int getPortalId() {
    return portal_id;
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

  @Override
  public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
    buf.writeInt(ColorPortal.getCost(portal_id));
    buf.writeGlobalPos(GlobalPos.create(this.world.getRegistryKey(), this.pos));
  }
}
