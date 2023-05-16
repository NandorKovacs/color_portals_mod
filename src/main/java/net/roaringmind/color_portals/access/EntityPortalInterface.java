package net.roaringmind.color_portals.access;

import net.minecraft.util.math.BlockPos;

public interface EntityPortalInterface {
  // used for casting of mixin
  public void setInColorPortal(BlockPos pos, int portal_id);
}
