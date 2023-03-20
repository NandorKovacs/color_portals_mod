package net.roaringmind.color_portals.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.screen.ColorPortalActivationScreenHandler;

public class ColorPortalLinkingScreen extends HandledScreen<ColorPortalActivationScreenHandler> {

  public ColorPortalLinkingScreen(ColorPortalActivationScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
  }

  @Override
  protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, ColorPortals.LINKING_GUI_TEXTURE);
    int x = (width - backgroundWidth) / 2;
    int y = (height - backgroundHeight) / 2;
    drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
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
    // center the title
    titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

    this.addDrawableChild(
        new TexturedButtonWidget(this.x + 61, this.y + 49, 55, 19, 0, 0, ColorPortals.LINKING_BUTTON_TEXTURE, null));
  }

  @Override
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawForeground(matrices, mouseX, mouseY);
    
    int textX = (backgroundWidth - textRenderer.getWidth("Link")) / 2;
    this.textRenderer.draw(matrices, "Link", textX, 54, 0);
  }
}
