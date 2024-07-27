package com.thuverx.resource;

import com.thuverx.resource.entity.ClientEntity;
import com.thuverx.render.model.GeoModel;
import com.thuverx.resource.model.Geometry;
import com.thuverx.resource.structure.support.KeyedMap;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BedrockResourcePackRegistries {
    public static final KeyedMap<ClientEntity> CLIENT_ENTITY_REGISTRY = new KeyedMap<>();
    public static final KeyedMap<String> ANIMATION_REGISTRY = new KeyedMap<>();
    public static final KeyedMap<String> ANIMATION_CONTROLLER_REGISTRY = new KeyedMap<>();
    public static final KeyedMap<Geometry> GEOMETRY_REGISTRY = new KeyedMap<>();
    public static final KeyedMap<String> MATERIAL_REGISTRY = new KeyedMap<>();
    public static final KeyedMap<Identifier> TEXTURE_REGISTRY = new KeyedMap<>();
    public static final List<String> SPLASHES_REGISTRY = new ArrayList<>();
}
