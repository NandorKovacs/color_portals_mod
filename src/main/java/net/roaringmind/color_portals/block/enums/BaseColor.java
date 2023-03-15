package net.roaringmind.color_portals.block.enums;

import java.util.function.IntFunction;

import net.minecraft.item.DyeItem;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

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
  NONE(16, "none");

  private static final IntFunction<BaseColor> BY_ID;
  private final int id;
  private final String name;

  private BaseColor(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return this.id;
  }

  public static BaseColor byId(int id) {
    return BY_ID.apply(id);
  }

  @Override
  public String asString() {
    return this.name;
  }

  public static BaseColor byDyeItem(DyeItem item) {
    return byId(item.getColor().getId());
  }

  static {
    BY_ID = ValueLists.createIdToValueFunction(BaseColor::getId, BaseColor.values(), ValueLists.OutOfBoundsHandling.ZERO);
  }
}
