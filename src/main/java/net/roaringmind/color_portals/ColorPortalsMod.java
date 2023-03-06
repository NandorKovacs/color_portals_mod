package net.roaringmind.color_portals;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorPortalsMod implements ModInitializer {
  public static String MODID = "color_portals";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  // block which is needed to activate the portal
  public static final Block COLOR_PORTAL_BASE = new Block(FabricBlockSettings.of(Material.METAL).requiresTool().strength(50.0f, 1200.0f));
  
  public static final Identifier COLOR_PORTAL_BASE_ID = new Identifier(MODID, "color_portal_base");


	@Override
	public void onInitialize() {
    Registry.register(Registries.BLOCK, COLOR_PORTAL_BASE_ID, COLOR_PORTAL_BASE);
    Registry.register(Registries.ITEM, COLOR_PORTAL_BASE_ID, new BlockItem(COLOR_PORTAL_BASE, new FabricItemSettings()));

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}
