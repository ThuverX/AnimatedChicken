package com.thuverx.resource.entity;

import com.thuverx.Constants;
import com.thuverx.resource.BedrockResourcePackRegistries;
import com.thuverx.resource.model.Geometry;
import com.thuverx.resource.structure.entity.EntityJSON;
import com.thuverx.resource.structure.support.KeyedMap;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ClientEntity {

    private String identifier;
    private KeyedMap<Geometry> models = new KeyedMap<>();
    private KeyedMap<Identifier> textures = new KeyedMap<>();

    public static ClientEntity of(EntityJSON definition) {
        if(definition == null) return null;
        if(definition.client_entity == null) return null;
        if(definition.client_entity.description.min_engine_version != null && !definition.client_entity.description.min_engine_version.isOlderThanOrEqualTo(Constants.ENGINE_VERSION)) {
            Constants.LOG.warn("Entity {} is incompatible with the current engine version (got {} but engine is {})", definition.client_entity.description.identifier, definition.client_entity.description.min_engine_version, Constants.ENGINE_VERSION);
            return null;
        }

        ClientEntity entity = new ClientEntity();
        entity.identifier = definition.client_entity.description.identifier;

        for(Map.Entry<String, String> entry : definition.client_entity.description.geometry.entrySet()) {
            String state = entry.getKey();
            String geometry = entry.getValue();

            if(!BedrockResourcePackRegistries.GEOMETRY_REGISTRY.containsKey(geometry)) continue;

            entity.models.put(state, BedrockResourcePackRegistries.GEOMETRY_REGISTRY.get(geometry));
        }

        for(Map.Entry<String, String> entry : definition.client_entity.description.textures.entrySet()) {
            String state = entry.getKey();
            String texture = entry.getValue();

            if(!BedrockResourcePackRegistries.TEXTURE_REGISTRY.containsKey(texture))  continue;

            entity.textures.put(state, BedrockResourcePackRegistries.TEXTURE_REGISTRY.get(texture));
        }

        return entity;
    }

    public String getIdentifier() {
        return identifier;
    }

    public KeyedMap<Geometry> getModels() {
        return models;
    }

    public Geometry getModel(String key) {
        return models.get(key);
    }

    public KeyedMap<Identifier> getTextures() {
        return textures;
    }
}
