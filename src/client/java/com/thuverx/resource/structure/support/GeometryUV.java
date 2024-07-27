package com.thuverx.resource.structure.support;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Vector2f;

import java.lang.reflect.Type;

public class GeometryUV {
    public static class SidedUV {
        public Vector2f uv;
        public Vector2f uv_size;
    }

    public int u = 0;
    public int v = 0;
    public KeyedMap<SidedUV> sides = new KeyedMap<>();

    public boolean isSided() {
        return !sides.isEmpty();
    }

    public static class Adapter implements JsonDeserializer<GeometryUV> {
        @Override
        public GeometryUV deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            GeometryUV geometryUV = new GeometryUV();
            if (json.isJsonArray()) {
                geometryUV.u = json.getAsJsonArray().get(0).getAsInt();
                geometryUV.v = json.getAsJsonArray().get(1).getAsInt();
            } else if (json.isJsonObject()) {
                geometryUV.sides = context.deserialize(json, KeyedMap.class);
            }
            return geometryUV;
        }
    }
}
