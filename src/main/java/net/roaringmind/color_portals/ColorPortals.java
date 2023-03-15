package net.roaringmind.color_portals;

import javax.swing.text.html.parser.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.block.ColorPortalBlock;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;

public class ColorPortals implements ModInitializer {
  public static final String MODID = "color_portals";

  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  // block which is needed to activate the portal
  public static final Identifier COLOR_PORTAL_BASE_ID;
  public static final Block COLOR_PORTAL_BASE;
  public static final Item COLOR_PORTAL_BASE_ITEM;
  public static final BlockEntityType<ColorPortalBaseEntity> COLOR_PORTAL_BASE_ENTITY;
  public static final ScreenHandlerType<ColorPortalActivationScreenHandler> COLOR_PORTAL_SCREEN_HANDLER;

  public static final Identifier COLOR_PORTAL_BLOCK_ID;
  public static final ColorPortalBlock COLOR_PORTAL_BLOCK;
  public static final BlockEntityType<ColorPortalBlockEntity> COLOR_PORTAL_BLOCK_ENTITY;

  public static final Identifier DRAGON_EYE_ID;
  public static final Item DRAGON_EYE;

  private static final Identifier ENDER_DRAGON_LOOT_TABLE_ID;


  static {

    // register base
    COLOR_PORTAL_BASE_ID = new Identifier(MODID, "color_portal_base");
    COLOR_PORTAL_BASE = Registry.register(Registries.BLOCK, COLOR_PORTAL_BASE_ID,
        new ColorPortalBase(
            FabricBlockSettings.of(Material.METAL).requiresTool().strength(50.0f, 1200.0f).nonOpaque()));
    COLOR_PORTAL_BASE_ITEM = Registry.register(Registries.ITEM, COLOR_PORTAL_BASE_ID,
        new BlockItem(COLOR_PORTAL_BASE, new FabricItemSettings()));
    COLOR_PORTAL_BASE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_PORTAL_BASE_ID,
        FabricBlockEntityTypeBuilder.create(ColorPortalBaseEntity::new, COLOR_PORTAL_BASE).build(null));

    // register portal block
    COLOR_PORTAL_BLOCK_ID = new Identifier(MODID, "color_portal_block");
    COLOR_PORTAL_BLOCK = Registry.register(Registries.BLOCK, COLOR_PORTAL_BLOCK_ID,
        new ColorPortalBlock(FabricBlockSettings.of(Material.PORTAL).noCollision().strength(-1.0f)
            .sounds(BlockSoundGroup.GLASS).luminance(state -> 11)));
    COLOR_PORTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_PORTAL_BLOCK_ID,
        FabricBlockEntityTypeBuilder.create(ColorPortalBlockEntity::new, COLOR_PORTAL_BLOCK).build(null));

    // register screen handler
    COLOR_PORTAL_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, COLOR_PORTAL_BASE_ID,
        new ScreenHandlerType<ColorPortalActivationScreenHandler>(ColorPortalActivationScreenHandler::new));

    // register item
    DRAGON_EYE_ID = new Identifier(MODID, "dragon_eye");
    DRAGON_EYE = Registry.register(Registries.ITEM, DRAGON_EYE_ID, new Item(new FabricItemSettings()));

    // make ender dragon drop DragonEye
    ENDER_DRAGON_LOOT_TABLE_ID = EntityType.ENDER_DRAGON.getLootTableId();
    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
      if (source.isBuiltin() && ENDER_DRAGON_LOOT_TABLE_ID.equals(id)) {
        LootPool.Builder poolBuilder = LootPool.builder()
          .with(ItemEntry.builder(DRAGON_EYE));

        tableBuilder.pool(poolBuilder);

      }
    });
  }

  @Override
  public void onInitialize() {

    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Hello Fabric world!");
  }
}
