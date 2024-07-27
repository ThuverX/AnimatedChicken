package com.thuverx.resource;

import com.thuverx.Constants;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BedrockResourcePackReloadListener implements SimpleResourceReloadListener<List<BedrockResourcePack>> {

    public BedrockResourcePackReloadListener() {
        ensureDirectory();
    }

    private void ensureDirectory() {
        if (BedrockResourcePack.BEDROCK_PACK_FOLDER.exists()) return;
        if (BedrockResourcePack.BEDROCK_PACK_FOLDER.mkdir()) return;

        Constants.LOG.error("Failed to create {} folder", BedrockResourcePack.BEDROCK_PACK_FOLDER.getName());
    }

    @Override
    public CompletableFuture<List<BedrockResourcePack>> load(ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(BedrockResourcePack::unloadAll, executor)
                .thenRunAsync(BedrockResourcePack::scan)
                .thenRunAsync(BedrockResourcePack::loadAll)
                .thenApply(unused -> BedrockResourcePack.resourcePacks);
    }

    @Override
    public CompletableFuture<Void> apply(List<BedrockResourcePack> data, ResourceManager manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(BedrockResourcePack::applyAll, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Identifier.of("animatedchicken:bedrock_resource_pack_reload_listener");
    }
}
