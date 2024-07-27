package com.thuverx.molang;

import com.google.gson.*;

import java.lang.reflect.Type;

public class Query {
//    private MolangExpression expression;
    private float value = 0;
    private boolean isStatic = false;
    private String query;

    public float getValue() {
        return value;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getQuery() {
        return query;
    }

//    public float evaluate(MolangEnvironment env) {
//        if(isStatic)
//            return value;
//
//        return env.safeResolve(expression);
//    }

    public void setQuery(String query) {
        this.query = query;
//        this.expression = McPackSupporterClient.safeCompile(query);
    }

    public static class Adapter implements JsonDeserializer<Query> {

        @Override
        public Query deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if(!json.isJsonPrimitive()) throw new JsonParseException("Value must be a primitive type");

            Query valueDefinition = new Query();
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();

            if(jsonPrimitive.isNumber()) {
                valueDefinition.value = jsonPrimitive.getAsFloat();
                valueDefinition.isStatic = true;
                valueDefinition.query = null;
                return valueDefinition;
            } else if(jsonPrimitive.isString()) {
                valueDefinition.value = 0;
                valueDefinition.isStatic = false;
                valueDefinition.setQuery(jsonPrimitive.getAsString());
                return valueDefinition;
            }

            return null;
        }
    }
}
