package com.thuverx.resource.structure.support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class StringOrRecord {
    public String value;
    public String key;
    public boolean hasKey;

    public String getLeft() {
        return hasKey ? key : value;
    }

    public String getRight() {
        return hasKey ? value : null;
    }

    public static class Adapter implements JsonDeserializer<StringOrRecord> {

        @Override
        public StringOrRecord deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            StringOrRecord stringOrRecord = new StringOrRecord();
            if(json.isJsonPrimitive()) {
                stringOrRecord.value = json.getAsString();
                return stringOrRecord;
            } else {
                stringOrRecord.hasKey = true;
                stringOrRecord.key = json.getAsJsonObject().entrySet().iterator().next().getKey();
                stringOrRecord.value = json.getAsJsonObject().get(stringOrRecord.key).getAsString();
                return stringOrRecord;
            }
        }
    }
}
