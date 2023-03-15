package net.roaringmind.color_portals.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.client.screen.ColorPortalActivationScreen;

public class ClientColorPortals implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    HandledScreens.register(ColorPortals.COLOR_PORTAL_SCREEN_HANDLER, ColorPortalActivationScreen::new);
  
    BlockRenderLayerMap.INSTANCE.putBlock(ColorPortals.COLOR_PORTAL_BASE, RenderLayer.getCutout());
  }

}
