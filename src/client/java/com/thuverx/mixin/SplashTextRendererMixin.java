package com.thuverx.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextRenderer.class)
public class SplashTextRendererMixin {

    @Final
    @Shadow
    private String text;



    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", shift = At.Shift.BEFORE), cancellable = true)
    private void render(DrawContext context, int screenWidth, TextRenderer textRenderer, int alpha, CallbackInfo ci) {
        List<OrderedText> splitStrings = textRenderer.wrapLines(Text.of(this.text), 200);

        for(int i = 0; i < splitStrings.size(); i++) {
            context.drawCenteredTextWithShadow(textRenderer, splitStrings.get(i), 0, -8 + i * 8, 0xffff00 | alpha);
        }

        context.getMatrices().pop();
        ci.cancel();
    }
}
