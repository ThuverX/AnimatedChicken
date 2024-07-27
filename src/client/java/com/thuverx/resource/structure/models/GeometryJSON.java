package com.thuverx.resource.structure.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.thuverx.Constants;
import com.thuverx.resource.structure.support.GeometryUV;
import com.thuverx.resource.structure.support.KeyedMap;
import com.thuverx.resource.structure.support.SemVer;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeometryJSON {

    public static class Geometry {
        public static class Description {
            public String identifier;
            public int texture_width;
            public int texture_height;
            public int visible_bounds_width;
            public int visible_bounds_height;
            public Vector3f visible_bounds_offset;
        }

        public static class Bone {
            public static class Cube {
                public Vector3f origin = new Vector3f();
                public Vector3f size = new Vector3f();
                public Vector3f pivot = new Vector3f();
                public Vector3f rotation = new Vector3f();
                public boolean mirror = false;
                public GeometryUV uv;
                public double inflate = 0;
            }
            public String name;
            public Vector3f pivot = new Vector3f();
            public Vector3f rotation = new Vector3f();
            public String parent;
            public boolean mirror = false;
            public String binding;
            public KeyedMap<Vector3f> locators = new KeyedMap<>();
            public List<Cube> cubes = new ArrayList<>();
        }

        public Description description;
        public List<Bone> bones = new ArrayList<>();
    }
    public SemVer format_version;
    public List<Geometry> geometries = new ArrayList<>();

    // TODO: loading of different format versions
    // check cow.geo.json for example of geometry file 1.8.0

    public static class Adapter implements JsonDeserializer<GeometryJSON> {
        @Override
        public GeometryJSON deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            GeometryJSON geometry = new GeometryJSON();

            JsonObject root = json.getAsJsonObject();
            SemVer version = SemVer.fromString(root.get("format_version").getAsString());

            geometry.format_version = version;
            geometry.geometries = new ArrayList<>();

            if(version.isEqualTo("1.12.0")) {
                geometry.geometries = parseV1120(root, context);
            } else if(version.isEqualTo("1.8.0")){
                geometry.geometries = parseV180(root, context);
            } else {
                throw new JsonParseException("Unsupported geometry format version: " + version);
            }

            return geometry;
        }

        private List<Geometry> parseV1120(JsonObject root, JsonDeserializationContext context) {
            return context.deserialize(root.get(Constants.Identifiers.MINECRAFT_GEOMETRY), new TypeToken<List<Geometry>>(){}.getType());
        }

        private List<Geometry> parseV180(JsonObject root, JsonDeserializationContext context) {
            List<Geometry> geometries = new ArrayList<>();

            for(Map.Entry<String, JsonElement> entry: root.entrySet()) {
                if(entry.getKey().equals("format_version")) continue;

                JsonObject element = entry.getValue().getAsJsonObject();

                Geometry geometry = new Geometry();
                geometry.description = new Geometry.Description();
                geometry.description.identifier = entry.getKey();
                geometry.description.texture_width = element.get("texturewidth").getAsInt();
                geometry.description.texture_height = element.get("textureheight").getAsInt();

                if(element.get("visible_bounds_width") != null)
                    geometry.description.visible_bounds_width = element.get("visible_bounds_width").getAsInt();
                if(element.get("visible_bounds_height") != null)
                    geometry.description.visible_bounds_height = element.get("visible_bounds_height").getAsInt();
                if(element.get("visible_bounds_offset") != null)
                    geometry.description.visible_bounds_offset = context.deserialize(element.get("visible_bounds_offset"), Vector3f.class);

                geometry.bones = context.deserialize(element.get("bones"), new TypeToken<List<Geometry.Bone>>(){}.getType());

                geometries.add(geometry);
            }

            return geometries;
        }
    }

    // TODO: Some files use "poly mesh" along with cubes, add support for that
}
