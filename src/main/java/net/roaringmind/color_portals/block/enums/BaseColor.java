package net.roaringmind.color_portals.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BaseColor implements StringIdentifiable {
  WHITE(0, "white"),
  ORANGE(1, "orange"),
  MAGENTA(2, "magenta"),
  LIGHT_BLUE(3, "light_blue"),
  YELLOW(4, "yellow"),
  LIME(5, "lime"),
  PINK(6, "pink"),
  GRAY(7, "gray"),
  LIGHT_GRAY(8, "light_gray"),
  CYAN(9, "cyan"),
  PURPLE(10, "purple"),
  BLUE(11, "blue"),
  BROWN(12, "brown"),
  GREEN(13, "green"),
  RED(14, "red"),
  BLACK(15, "black"),
  NONE(-1, "none");

  private final int id;
  private final String name;

  private BaseColor(int id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String asString() {
      return this.name;
  }
}
