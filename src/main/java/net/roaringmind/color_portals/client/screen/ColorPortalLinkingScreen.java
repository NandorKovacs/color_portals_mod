package net.roaringmind.color_portals.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.roaringmind.color_portals.ColorPortals;
import net.roaringmind.color_portals.screen.ColorPortalLinkingScreenHandler;

public class ColorPortalLinkingScreen extends HandledScreen<ColorPortalLinkingScreenHandler> {

  public ColorPortalLinkingScreen(ColorPortalLinkingScreenHandler handler, PlayerInventory inventory, Text title) {
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
    int cost = ((ColorPortalLinkingScreenHandler) this.handler).getCost();    

    String text = "Link";
    int textX = (backgroundWidth - textRenderer.getWidth(text)) / 2;
    int color = 0;
    this.textRenderer.draw(matrices, text, textX, 54, color);

    text = "cost: " + String.valueOf(cost);
    textX = (backgroundWidth - textRenderer.getWidth(text)) / 2;
    color = 0xFF6060;
    if (this.client.player.experienceLevel >= cost) {
      color = 8453920;
    }
    this.textRenderer.drawWithShadow(matrices, text, textX, 34, color);
  }
}
