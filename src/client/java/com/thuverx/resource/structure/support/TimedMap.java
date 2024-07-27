package com.thuverx.resource.structure.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimedMap<T> extends HashMap<Float, T> {
    public List<T> getFrames(float time) {
        List<T> frames = new ArrayList<>();

        for(Float key : keySet()) {
            if(key <= time) {
                frames.add(get(key));
            }
        }

        return frames;
    }

    public T getFrame(float time) {
        for(Float key : keySet().stream().sorted().toList()) {
            if(key >= time) {
                return get(key);
            }
        }
        return null;
    }

    public T getNextFrame(float time) {
        T currentFrame = getFrame(time);
        if(currentFrame == null) {
            return null;
        }

        boolean next = false;

        for(Float key : keySet()) {
            if(next) {
                return get(key);
            }

            if(key == time) {
                next = true;
            }
        }

        return null;
    }

    public float getNearestLowerFrame(float time) {
        float frame = 0;

        for(Float key : keySet().stream().sorted().toList()) {
            if(key >= time) {
                return frame;
            }

            frame = key;
        }

        return frame;
    }

    public float getNearestUpperFrame(float time) {
        for(Float key : keySet().stream().sorted().toList()) {
            if(key >= time) {
                return key;
            }
        }

        return 0;
    }

    public record Stage<T> (T a, T b, float start, float end){
        public float getProgress(float time) {
            if(start == end) return 0;
            if(end - start == 0) return 0;

            return (time - start) / (end - start);
        }
    }

    public Stage<T> getStage(float time) {
        float a = getNearestLowerFrame(time);
        float b = getNearestUpperFrame(time);


        if(!containsKey(a) && !containsKey(b)) {
            return null;
        }
        else if(!containsKey(a)) {
            return new Stage<>(get(b), get(b), b, b);
        }
        else if(!containsKey(b)) {
            return new Stage<>(get(a), get(a), a, a);
        }

        return new Stage<>(get(a), get(b), a, b);
    }
}
