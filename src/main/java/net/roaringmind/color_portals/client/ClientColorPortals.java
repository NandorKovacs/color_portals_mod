package net.roaringmind.color_portals.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.client.screen.ColorPortalScreen;

public class ClientColorPortals implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    HandledScreens.register(ColorPortals.COLOR_PORTAL_SCREEN_HANDLER, ColorPortalScreen::new);
  }

}
