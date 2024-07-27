package com.thuverx.gui;

import com.thuverx.Constants;
import com.thuverx.resource.BedrockResourcePack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class BedrockPackWidget extends ClickableWidget {
    public static Identifier UNKNOWN_PACK = Identifier.of(Constants.MOD_ID, "unknown_pack");
    public static int WIDTH = 256;
    public static int HEIGHT = 48;

    private final BedrockResourcePack resourcePack;

    public BedrockPackWidget(int x, int y, BedrockResourcePack resourcePack) {
        super(x, y, WIDTH, HEIGHT, ScreenTexts.EMPTY);
        this.resourcePack = resourcePack;
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.drawTexture(this.resourcePack.getImage(),0,0, this.getX(), this.getY(), HEIGHT, HEIGHT);

        MultilineText multilineText = MultilineText.create(textRenderer, this.resourcePack.getName(), WIDTH - HEIGHT - 8);
        multilineText.draw(context, this.getX() + HEIGHT + 4, this.getY() + 4, textRenderer.fontHeight, 0xFFFFFF);

        int height = multilineText.count() * textRenderer.fontHeight;

        multilineText = MultilineText.create(textRenderer, this.resourcePack.getDescription(), WIDTH - HEIGHT - 8);
        multilineText.draw(context, this.getX() + HEIGHT + 4, this.getY() + 4 + height, textRenderer.fontHeight, 0xAAAAAA);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}