package com.thuverx.entity;

import com.thuverx.events.BedrockPacksAppliedCallback;
import com.thuverx.resource.BedrockResourcePackRegistries;
import com.thuverx.resource.entity.ClientEntity;
import com.thuverx.render.model.GeoModel;
import com.thuverx.resource.model.Geometry;
import com.thuverx.resource.structure.support.KeyedMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Quaternionf;

import java.util.*;

public class LocalEntity {

    public static HashMap<UUID, LocalEntity> localEntities = new HashMap<>();
    private final LivingEntity livingEntity;
    private ClientEntity clientEntity;
    private final KeyedMap<GeoModel> models = new KeyedMap<>();
    private final KeyedMap<Identifier> textures = new KeyedMap<>();

    public LocalEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;

        load();
        BedrockPacksAppliedCallback.EVENT.register(this::load);
    }

    private void load() {
        this.clientEntity = BedrockResourcePackRegistries.CLIENT_ENTITY_REGISTRY.get(getId());

        if(clientEntity == null) return;

        for(Map.Entry<String, Geometry> entry : clientEntity.getModels().entrySet()) {
            models.put(entry.getKey(), GeoModel.of(entry.getValue()));
        }

        textures.putAll(clientEntity.getTextures());
    }

    public Pair<GeoModel, Identifier> getModel(String state) {
        return new Pair<>(models.get(state), textures.get(state));
    }

    public boolean hasBedrock() {
        return clientEntity != null;
    }

    public void update() {
        if(livingEntity.isRemoved()) {
            remove(livingEntity);
        }
    }

    public void render(float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        Pair<GeoModel, Identifier> modelPair = getModel("default");
        if(modelPair == null || modelPair.getLeft() == null) return;

        RenderLayer renderLayer = RenderLayer.getEntityCutoutNoCull(modelPair.getRight());

        if (renderLayer == null) return;

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
        int overlay = LivingEntityRenderer.getOverlay(livingEntity, 0);

        matrixStack.push();
        matrixStack.multiply(new Quaternionf().rotateY((float) Math.toRadians(-yaw + 180)));

        modelPair.getLeft().render(matrixStack, vertexConsumer, light, overlay);

        matrixStack.pop();
    }

    public String getId() {
        return EntityType.getId(livingEntity.getType()).toString();
    }

    public static LocalEntity get(LivingEntity uuid) {
        return localEntities.get(uuid.getUuid());
    }

    public static void remove(LivingEntity entity) {
        localEntities.remove(entity.getUuid());
    }

    public static List<LocalEntity> getAll() {
        return new ArrayList<>(localEntities.values());
    }

    public static LocalEntity getOrCreate(LivingEntity entity) {
        return localEntities.computeIfAbsent(entity.getUuid(), uuid -> new LocalEntity(entity));
    }
}
