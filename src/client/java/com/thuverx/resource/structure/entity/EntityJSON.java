package com.thuverx.resource.structure.entity;

import com.google.gson.annotations.SerializedName;
import com.thuverx.Constants;
import com.thuverx.resource.structure.support.KeyedMap;
import com.thuverx.resource.structure.support.SemVer;
import com.thuverx.resource.structure.support.StringOrRecord;

import java.util.List;

public class EntityJSON {
    public static class ClientEntity {
        public static class Description {
            public static class SpawnEgg {
                public String texture;
                public int texture_index;
                public String base_color;
                public String overlay_color;
            }

            public static class Scripts {
                public List<String> initialize;
                public List<StringOrRecord> pre_animation;
                public List<StringOrRecord> animate;
            }

            public String identifier;
            public SemVer min_engine_version;
            public KeyedMap<String> materials;
            public KeyedMap<String> textures;
            public KeyedMap<String> geometry;
            public KeyedMap<String> animations;
            public KeyedMap<String> animations_controller;
            public KeyedMap<String> sound_effects;
            public Scripts scripts;
            public List<StringOrRecord> render_controllers;
            public KeyedMap<String> particle_effects;
            public SpawnEgg spawn_egg;
            public boolean enable_attachables;
            public boolean hide_armor;
        }

        public Description description;
    }

    public SemVer format_version;
    @SerializedName(Constants.Identifiers.MINECRAFT_CLIENT_ENTITY)
    public ClientEntity client_entity;
}
