package net.roaringmind.color_portals.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.roaringmind.color_portals.ColorPortals;

public class ColorPortalBlockEntity extends BlockEntity {
  private int portal_id = -1;

  public ColorPortalBlockEntity(BlockPos pos, BlockState state) {
    super(ColorPortals.COLOR_PORTAL_BLOCK_ENTITY, pos, state);
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
