package net.roaringmind.color_portals.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;

public class ColorPortalActivationScreen extends HandledScreen<ColorPortalActivationScreenHandler>{

  public ColorPortalActivationScreen(ColorPortalActivationScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, ColorPortals.ACTIVATION_GUI_TEXTURE);
    int x = (width - backgroundWidth) / 2;
    int y = (height - backgroundHeight) / 2;
    drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
  
    Slot slot = this.handler.getSlot();
    if (!slot.hasStack()) {
      this.drawTexture(matrices, x + slot.x, y + slot.y, this.backgroundWidth, 0, 16, 16);
    }
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    super.render(matrices, mouseX, mouseY, delta);
    drawMouseoverTooltip(matrices, mouseX, mouseY);
  }

  @Override
  protected void init() {
    super.init();
    // Center the title
    titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
  }
}
