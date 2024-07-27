package com.thuverx.mixin.screen;

import com.thuverx.gui.BedrockPackWidget;
import com.thuverx.resource.BedrockResourcePack;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackScreen.class)
public class PackScreenMixin extends Screen {
    protected PackScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private final List<Element> addedElements = new ArrayList<>();

    @Inject(at = @At("TAIL"), method = "init()V")
    public void init(CallbackInfo ci) {
        addedElements.forEach(this::remove);

        for (BedrockResourcePack pack : BedrockResourcePack.resourcePacks) {
            addedElements.add(this.addDrawable(new BedrockPackWidget(0,0, pack)));
        }
    }
}

