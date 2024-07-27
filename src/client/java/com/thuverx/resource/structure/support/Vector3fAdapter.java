package com.thuverx.resource.structure.support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Vector3fAdapter implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Vector3f vector3 = new Vector3f();
        vector3.x = json.getAsJsonArray().get(0).getAsFloat();
        vector3.y = json.getAsJsonArray().get(1).getAsFloat();
        vector3.z = json.getAsJsonArray().get(2).getAsFloat();
        return vector3;
    }
}