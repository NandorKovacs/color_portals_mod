package net.roaringmind.color_portals;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class ColorPortalRegistry extends PersistentState {
  ColorPortal[] list;

  public ColorPortalRegistry() {
    list = new ColorPortal[32];
    for (int i = 0; i < 32; ++i) {
      list[i] = null;
    }
  }

  public void removePortal(int id) {
    markDirty();
    list[id] = null;
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

  public Pair<ColorPortal, ColorPortal> getByColor(int id) {
    int color_id = id - id % 2;
    if (color_id > 15) {
      return null;
    }

    return new Pair<ColorPortal, ColorPortal>(list[color_id], list[color_id + 1]);
  }

  public static ColorPortalRegistry createFromNbt(NbtCompound tag) {
    NbtCompound compound = tag.getCompound(ColorPortals.MODID);
    ColorPortalRegistry res = new ColorPortalRegistry();
    for (int i = 0; i < 32; ++i) {
      res.list[i] = ColorPortal.createFromNbt(compound.getCompound(String.valueOf(i)));
    }
    return res;
  }
  @Override
  public NbtCompound writeNbt(NbtCompound var1) {
    NbtCompound compound = new NbtCompound();
    for (int i = 0; i < 32; ++i) {
      NbtCompound portal_compound = new NbtCompound();
      if (list[i] != null) {
        portal_compound = list[i].writeNbt();
      }

      compound.put(String.valueOf(i), portal_compound);
    }
  
    var1.put(ColorPortals.MODID, compound);
    return var1;
  }
}