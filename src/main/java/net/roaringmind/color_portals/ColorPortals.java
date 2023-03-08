package net.roaringmind.color_portals;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.screen.ColorPortalScreenHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorPortals implements ModInitializer {
  public static String MODID = "color_portals";

  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  // block which is needed to activate the portal
  public static final Identifier COLOR_PORTAL_BASE_ID;
  public static final Block COLOR_PORTAL_BASE;
  public static final Item COLOR_PORTAL_BASE_ITEM;
  public static final BlockEntityType<ColorPortalBaseEntity> COLOR_PORTAL_BASE_ENTITY;
  public static final ScreenHandlerType<ColorPortalScreenHandler> COLOR_PORTAL_SCREEN_HANDLER;

  static {
    COLOR_PORTAL_BASE_ID = new Identifier(MODID, "color_portal_base");
    COLOR_PORTAL_BASE = Registry.register(Registries.BLOCK, COLOR_PORTAL_BASE_ID,
        new ColorPortalBase(FabricBlockSettings.of(Material.METAL).requiresTool().strength(50.0f, 1200.0f)));
    COLOR_PORTAL_BASE_ITEM = Registry.register(Registries.ITEM, COLOR_PORTAL_BASE_ID,
        new BlockItem(COLOR_PORTAL_BASE, new FabricItemSettings()));
    COLOR_PORTAL_BASE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_PORTAL_BASE_ID,
        FabricBlockEntityTypeBuilder.create(ColorPortalBaseEntity::new, COLOR_PORTAL_BASE).build(null));
    COLOR_PORTAL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, COLOR_PORTAL_BASE_ID,
        new ScreenHandlerType<ColorPortalScreenHandler>(ColorPortalScreenHandler::new));
  }

  @Override
  public void onInitialize() {

    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Hello Fabric world!");
  }
}
