package com.thuverx.resource.structure.animation;

import com.google.gson.*;
import com.thuverx.molang.Query;
import com.thuverx.molang.VectorQuery;
import com.thuverx.resource.structure.support.KeyedMap;
import com.thuverx.resource.structure.support.SemVer;
import com.thuverx.resource.structure.support.TimedMap;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class AnimationJSON {
    public static class Animation {

        public static class KeyFrame {
            public VectorQuery value;
            public VectorQuery pre = null;
            public VectorQuery post = null;
            public String lerp_mode = null;

            public KeyFrame() {}
            public KeyFrame(VectorQuery value) {
                this.value = value;
            }

            public VectorQuery getAtStart() {
                if(post != null) return post;
                return value;
            }

            public VectorQuery getAtEnd() {
                if(pre != null) return pre;
                return value;
            }

            public static class Adapter implements JsonDeserializer<KeyFrame> {
                @Override
                public KeyFrame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    KeyFrame keyFrame = new KeyFrame();

                    if(json.isJsonArray()) {
                        keyFrame.value = context.deserialize(json, VectorQuery.class);
                    } else {
                        JsonObject root = json.getAsJsonObject();

                        if(root.has("pre")) {
                            keyFrame.pre = context.deserialize(root.get("pre"), VectorQuery.class);
                        }

                        if(root.has("post")) {
                            keyFrame.post = context.deserialize(root.get("post"), VectorQuery.class);
                        }

                        if(root.has("lerp_mode")) {
                            keyFrame.lerp_mode = root.get("lerp_mode").getAsString();
                        }
                    }

                    return keyFrame;
                }
            }
        }

        public static class AnimatedBone {
            public static class RelativitySetting {
                public String position = "absolute";
                public String rotation = "absolute";
                public String scale = "absolute";
            }
            public RelativitySetting relative_to = new RelativitySetting();
            public TimedMap<KeyFrame> position = new TimedMap<>();
            public TimedMap<KeyFrame> rotation = new TimedMap<>();
            public TimedMap<KeyFrame> scale = new TimedMap<>();

            public List<KeyFrame> getAllKeyFrames() {
                List<KeyFrame> keyFrames = new java.util.ArrayList<>();
                keyFrames.addAll(position.values());
                keyFrames.addAll(rotation.values());
                keyFrames.addAll(scale.values());
                return keyFrames;
            }

            public static class Adapter implements JsonDeserializer<AnimatedBone> {

                private static boolean isAllFloatKeys(JsonObject object) {
                    for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
                        try {
                            Float.parseFloat(entry.getKey());
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return true;
                }

                private static TimedMap<KeyFrame> parse(JsonObject object, String key, JsonDeserializationContext context) {
                    TimedMap<KeyFrame> timedMap = new TimedMap<>();
                    if(object.has(key)) {
                        JsonElement element = object.get(key);

                        if(element.isJsonObject()) {
                            JsonObject object1 = element.getAsJsonObject();
                            if(isAllFloatKeys(object1)) {
                                for(Map.Entry<String, JsonElement> entry : object1.entrySet()) {
                                    float time = Float.parseFloat(entry.getKey());
                                    timedMap.put(time, context.deserialize(entry.getValue(), KeyFrame.class));
                                }
                            } else {
                                timedMap.put(0f, context.deserialize(element, KeyFrame.class));
                            }
                        } else if(element.isJsonArray()) {
                            timedMap.put(0f, new KeyFrame(context.deserialize(element, VectorQuery.class)));
                        }
                    }

                    return timedMap;
                }

                @Override
                public AnimatedBone deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    AnimatedBone animatedBone = new AnimatedBone();

                    JsonObject root = json.getAsJsonObject();

                    animatedBone.position = parse(root, "position", context);
                    animatedBone.rotation = parse(root, "rotation", context);
                    animatedBone.scale = parse(root, "scale", context);

                    return animatedBone;
                }
            }
        }
        public static class SoundEffect {
            public String effect;
        }
        public String loop;
        public Query loop_delay;
        public Query anim_time_update;
        public float animation_length;
        public KeyedMap<AnimatedBone> bones;
        public TimedMap<SoundEffect> sound_effects;

        public List<KeyFrame> getAllKeyFrames() {
            List<KeyFrame> keyFrames = new java.util.ArrayList<>();
            if(this.bones == null) return keyFrames;

            for(AnimatedBone bone : bones.values()) {
                keyFrames.addAll(bone.getAllKeyFrames());
            }
            return keyFrames;
        }
    }

    public SemVer format_version;
    public KeyedMap<Animation> animations;
}
