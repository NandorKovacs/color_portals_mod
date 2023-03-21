package net.roaringmind.color_portals;

import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class ColorPortalRegistry extends PersistentState {
  private ColorPortal[] list;
  private Boolean[] links;

  public ColorPortalRegistry() {
    list = new ColorPortal[32];
    links = new Boolean[16];
    for (int i = 0; i < 32; ++i) {
      list[i] = null;
      if (i >= 16) {
        continue;
      }
      links[i] = false;
    }
  }

  public void removePortal(int id) {
    markDirty();
    list[id] = null;
    links[(id - id % 2) / 2] = false;
  }

  public void linkPortal(int id, UUID uuid) {
    if (list[id - id % 2] == null || list[id - id % 2 + 1] == null) {
      return;
    }

    links[(id - id % 2) / 2] = true;
    markDirty();
  }

  public int addPortal(ColorPortal portal, World world) {
    markDirty();
    int color_id = portal.getColor().getId();

    if (color_id > 15) {
      ColorPortals.LOGGER.warn("tried adding portal with color id > 15 to portal list");
      return -1;
    }

    ColorPortal a = list[color_id], b = list[color_id + 1];
    if (a == null) {
      list[color_id] = portal;
      return color_id;
    }
    if (b == null) {
      list[color_id + 1] = portal;
      return color_id + 1;
    }
    if (a.getAge() > b.getAge()) {
      a.destroy(world);
      list[color_id] = portal;
      return color_id;
    }
    b.destroy(world);
    list[color_id + 1] = portal;
    return color_id + 1;
  }

  public ColorPortal getById(int id) {
    return list[id];
  }

  public static ColorPortalRegistry createFromNbt(NbtCompound tag) {
    NbtCompound compound = tag.getCompound(ColorPortals.MODID);
    NbtCompound portals = compound.getCompound("portals");
    NbtCompound linkCompound = compound.getCompound("links");

    ColorPortalRegistry res = new ColorPortalRegistry();
    for (int i = 0; i < 32; ++i) {
      res.list[i] = ColorPortal.createFromNbt(portals.getCompound(String.valueOf(i)));
    }
    for (int i = 0; i < 16; ++i) {
      res.links[i] = linkCompound.getBoolean(String.valueOf(i));
    }
    return res;
  }

  @Override
  public NbtCompound writeNbt(NbtCompound var1) {
    NbtCompound compound = new NbtCompound();
    NbtCompound portals = new NbtCompound();
    for (int i = 0; i < 32; ++i) {
      NbtCompound portal_compound = new NbtCompound();
      if (list[i] != null) {
        portal_compound = list[i].writeNbt();
      }

      portals.put(String.valueOf(i), portal_compound);
    }

    NbtCompound linkCompound = new NbtCompound();
    for (int i = 0; i < 16; ++i) {
      linkCompound.putBoolean(String.valueOf(i), links[i]);
    }

    compound.put("portals", portals);
    compound.put("links", linkCompound);
    var1.put(ColorPortals.MODID, compound);
    return var1;
  }
}