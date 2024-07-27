package com.thuverx.resource.structure.animation_controllers;

import com.thuverx.resource.structure.support.KeyedMap;
import com.thuverx.resource.structure.support.SemVer;
import com.thuverx.resource.structure.support.StringOrRecord;

import java.util.ArrayList;
import java.util.List;

public class AnimationControllerJSON {

    public static class AnimationController {
        public static class State {
            public float blend_transition = 0;
            public List<StringOrRecord> animations = new ArrayList<>();
            public List<StringOrRecord> transitions = new ArrayList<>();
        }

        public KeyedMap<State> states;
        public String initial_state = "default";
    }

    public SemVer format_version;
    public KeyedMap<AnimationController> animation_controllers;
}
