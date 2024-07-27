package com.thuverx.resource.structure.support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Vector2f;

import java.lang.reflect.Type;

public class Vector2fAdapter implements JsonDeserializer<Vector2f> {
    @Override
    public Vector2f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Vector2f vector2 = new Vector2f();
        vector2.x = json.getAsJsonArray().get(0).getAsFloat();
        vector2.y = json.getAsJsonArray().get(1).getAsFloat();
        return vector2;
    }
}