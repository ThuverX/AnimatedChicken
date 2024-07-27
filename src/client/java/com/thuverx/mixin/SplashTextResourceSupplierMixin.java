package com.thuverx.mixin;

import com.google.common.collect.Lists;
import com.thuverx.events.BedrockPacksAppliedCallback;
import com.thuverx.resource.BedrockResourcePackRegistries;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.session.Session;
import net.minecraft.resource.SinglePreparationResourceReloader;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public abstract class SplashTextResourceSupplierMixin extends SinglePreparationResourceReloader<List<String>> {

    @Mutable
    @Shadow
    @Final
    private final List<String> splashTexts = Lists.newArrayList();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Session session, CallbackInfo ci) {
        BedrockPacksAppliedCallback.EVENT.register(this::apply);
    }

    @Unique
    private void apply() {
        if(BedrockResourcePackRegistries.SPLASHES_REGISTRY.isEmpty()) {
            return;
        }

        this.splashTexts.clear();
        this.splashTexts.addAll(BedrockResourcePackRegistries.SPLASHES_REGISTRY);
    }
}
