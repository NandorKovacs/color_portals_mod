package net.roaringmind.color_portals;

import net.minecraft.util.Pair;
import net.roaringmind.color_portals.block.enums.BaseColor;

public class ColorPortalRegistry {
  ColorPortal[] list;

  public ColorPortalRegistry() {
    list = new ColorPortal[32];
    for (int i = 0; i < 32; ++i) {
      list[i] = null;
    }
  }

  public int addPortal(ColorPortal portal) {
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
      a.destroy();
      list[color_id] = portal;
      return color_id;
    }
    b.destroy();
    list[color_id + 1] = portal;
    return color_id + 1;
  }

  public ColorPortal getById(int id) {
    return list[id];
  }

  public Pair<ColorPortal, ColorPortal> getByColor(BaseColor color) {
    int color_id = color.getId();
    if (color_id > 15) {
      return null;
    }

    return new Pair<ColorPortal, ColorPortal>(list[color_id], list[color_id + 1]);
  }
}