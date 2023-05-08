package net.roaringmind.color_portals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.roaringmind.color_portals.block.ColorPortalBase;
import net.roaringmind.color_portals.block.ColorPortalBlock;
import net.roaringmind.color_portals.block.entity.ColorPortalBaseEntity;
import net.roaringmind.color_portals.block.entity.ColorPortalBlockEntity;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;
import net.roaringmind.color_portals.screen.ColorPortalLinkingScreenHandler;

public class ColorPortals implements ModInitializer {
  public static final String MODID = "color_portals";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  // all Identifiers
  public static final Identifier ACTIVATION_GUI_TEXTURE;
  public static final Identifier LINKING_GUI_TEXTURE;
  public static final Identifier LINKING_BUTTON_TEXTURE;

  public static final Identifier COLOR_PORTAL_BASE_ID;
  public static final Identifier COLOR_PORTAL_BLOCK_ID;

  public static final Identifier ACTIVATION_SCREEN_HANDLER_ID;
  public static final Identifier LINKING_SCREEN_HANDLER_ID;

  public static final Identifier DRAGON_EYE_ID;
  public static final Identifier ENDER_DRAGON_LOOT_TABLE_ID;

  public static final Identifier LINK_PACKET_CHANNEL_ID;

  // statics for the base block of the portal
  public static final Block COLOR_PORTAL_BASE;
  public static final Item COLOR_PORTAL_BASE_ITEM;
  public static final BlockEntityType<ColorPortalBaseEntity> COLOR_PORTAL_BASE_ENTITY;

  // statics for the colored portal block
  public static final ColorPortalBlock COLOR_PORTAL_BLOCK;
  public static final BlockEntityType<ColorPortalBlockEntity> COLOR_PORTAL_BLOCK_ENTITY;

  // screenhandler types
  public static final ScreenHandlerType<ColorPortalActivationScreenHandler> COLOR_PORTAL_ACTIVATION_SCREEN_HANDLER;
  public static final ScreenHandlerType<ColorPortalLinkingScreenHandler> COLOR_PORTAL_LINKING_SCREEN_HANDLER;

  // statics for the dragon eye item required to craft a portal base
  public static final Item DRAGON_EYE;

  static {
    // register identifiers
    ACTIVATION_GUI_TEXTURE = new Identifier(ColorPortals.MODID, "textures/gui/color_portal_activation.png");
    LINKING_GUI_TEXTURE = new Identifier(ColorPortals.MODID, "textures/gui/color_portal_linking.png");
    LINKING_BUTTON_TEXTURE = new Identifier(ColorPortals.MODID, "textures/gui/linking_button.png");

    COLOR_PORTAL_BASE_ID = new Identifier(MODID, "color_portal_base");
    COLOR_PORTAL_BLOCK_ID = new Identifier(MODID, "color_portal_block");

    ACTIVATION_SCREEN_HANDLER_ID = new Identifier(MODID, "activation_screen");
    LINKING_SCREEN_HANDLER_ID = new Identifier(MODID, "linking_screen");

    DRAGON_EYE_ID = new Identifier(MODID, "dragon_eye");

    LINK_PACKET_CHANNEL_ID = new Identifier(MODID, "link_packet");

    // register base
    COLOR_PORTAL_BASE = Registry.register(Registries.BLOCK, COLOR_PORTAL_BASE_ID,
        new ColorPortalBase(
            FabricBlockSettings.of(Material.METAL).requiresTool().strength(50.0f, 1200.0f).nonOpaque()));
    COLOR_PORTAL_BASE_ITEM = Registry.register(Registries.ITEM, COLOR_PORTAL_BASE_ID,
        new BlockItem(COLOR_PORTAL_BASE, new FabricItemSettings()));
    COLOR_PORTAL_BASE_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_PORTAL_BASE_ID,
        FabricBlockEntityTypeBuilder.create(ColorPortalBaseEntity::new, COLOR_PORTAL_BASE).build(null));

    // register portal block
    COLOR_PORTAL_BLOCK = Registry.register(Registries.BLOCK, COLOR_PORTAL_BLOCK_ID,
        new ColorPortalBlock(FabricBlockSettings.of(Material.PORTAL).noCollision().strength(-1.0f)
            .sounds(BlockSoundGroup.GLASS).luminance(state -> 11)));
    COLOR_PORTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, COLOR_PORTAL_BLOCK_ID,
        FabricBlockEntityTypeBuilder.create(ColorPortalBlockEntity::new, COLOR_PORTAL_BLOCK).build(null));

    // register screen handler
    COLOR_PORTAL_ACTIVATION_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, ACTIVATION_SCREEN_HANDLER_ID,
        new ExtendedScreenHandlerType<ColorPortalActivationScreenHandler>(ColorPortalActivationScreenHandler::new));
    COLOR_PORTAL_LINKING_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, LINKING_SCREEN_HANDLER_ID,
        new ExtendedScreenHandlerType<ColorPortalLinkingScreenHandler>(ColorPortalLinkingScreenHandler::new));

    // register item
    DRAGON_EYE = Registry.register(Registries.ITEM, DRAGON_EYE_ID, new Item(new FabricItemSettings()));

    // register loot table modification for the ender dragon
    ENDER_DRAGON_LOOT_TABLE_ID = EntityType.ENDER_DRAGON.getLootTableId();
    LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
      if (source.isBuiltin() && ENDER_DRAGON_LOOT_TABLE_ID.equals(id)) {
        LootPool.Builder poolBuilder = LootPool.builder()
            .with(ItemEntry.builder(DRAGON_EYE));

        tableBuilder.pool(poolBuilder);

      }
    });
  }

  public static ColorPortalRegistry portalRegistry = new ColorPortalRegistry();

  @Override
  public void onInitialize() {
    LOGGER.info("Initializing!");

    ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
      portalRegistry = server.getWorld(World.OVERWORLD).getPersistentStateManager()
          .getOrCreate(ColorPortalRegistry::createFromNbt, ColorPortalRegistry::new, MODID);
    });
    portalRegistry.markDirty();

    ServerPlayNetworking.registerGlobalReceiver(ColorPortals.LINK_PACKET_CHANNEL_ID,
        (server, player, handler, buf, sender) -> {
          GlobalPos global_pos = buf.readGlobalPos();

          server.execute(() -> {
            World world = server.getWorld(global_pos.getDimension());
            BlockPos pos = global_pos.getPos();

            ColorPortals.portalRegistry.linkPortal(world, pos);
          });
        });
  }
}
