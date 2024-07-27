package com.thuverx.molang;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class VectorQuery {
    private Query x;
    private Query y;
    private Query z;

    public Query getX() {
        return x;
    }

    public Query getY() {
        return y;
    }

    public Query getZ() {
        return z;
    }

//    public Vector3f evaluate(MolangEnvironment env) {
//        return new Vector3f(x.evaluate(env), y.evaluate(env), z.evaluate(env));
//    }

    public static class Adapter implements JsonDeserializer<VectorQuery> {
        @Override
        public VectorQuery deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(json.isJsonObject()) {
                VectorQuery vectorDefinition = new VectorQuery();
                vectorDefinition.x = context.deserialize(json.getAsJsonObject().get("x"), Query.class);
                vectorDefinition.y = context.deserialize(json.getAsJsonObject().get("y"), Query.class);
                vectorDefinition.z = context.deserialize(json.getAsJsonObject().get("z"), Query.class);
                return vectorDefinition;
            } else if(json.isJsonArray()) {
                VectorQuery vectorDefinition = new VectorQuery();
                vectorDefinition.x = context.deserialize(json.getAsJsonArray().get(0), Query.class);
                vectorDefinition.y = context.deserialize(json.getAsJsonArray().get(1), Query.class);
                vectorDefinition.z = context.deserialize(json.getAsJsonArray().get(2), Query.class);
                return vectorDefinition;
            }

            throw new JsonParseException("Value must be a primitive type");
        }
    }
}
