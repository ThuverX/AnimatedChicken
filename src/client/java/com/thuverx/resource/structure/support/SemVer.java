package com.thuverx.resource.structure.support;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SemVer {
    public int major;
    public int minor;
    public int patch;

    public SemVer(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static SemVer fromString(String version) {
        String[] parts = version.split("\\.");

        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);

        return new SemVer(major, minor, patch);
    }

    public static SemVer of(String version) {
        return fromString(version);
    }

    public boolean isOlderThan(SemVer v) {
        if (major < v.major) return true;
        if (major > v.major) return false;
        if (minor < v.minor) return true;
        if (minor > v.minor) return false;
        return patch < v.patch;
    }

    public boolean isOlderThan(String s) {
        SemVer other = fromString(s);
        return isOlderThan(other);
    }

    public boolean isOlderThanOrEqualTo(SemVer other) {
        if (major < other.major) return true;
        if (major > other.major) return false;
        if (minor < other.minor) return true;
        if (minor > other.minor) return false;
        return patch <= other.patch;
    }

    public boolean isOlderThanOrEqualTo(String s) {
        SemVer other = fromString(s);
        return isOlderThanOrEqualTo(other);
    }

    public boolean isBetweenInclusive(SemVer lower, SemVer upper) {
        return !isOlderThan(lower) && !isNewerThan(upper);
    }

    public boolean isBetweenInclusive(String lower, String upper) {
        return !isOlderThan(lower) && !isNewerThan(upper);
    }

    public boolean isNewerThanOrEqualTo(SemVer other) {
        if (major > other.major) return true;
        if (major < other.major) return false;
        if (minor > other.minor) return true;
        if (minor < other.minor) return false;
        return patch >= other.patch;
    }

    public boolean isNewerThanOrEqualTo(String s) {
        SemVer other = fromString(s);
        return isNewerThanOrEqualTo(other);
    }

    public boolean isNewerThan(SemVer other) {
        if (major > other.major) return true;
        if (major < other.major) return false;
        if (minor > other.minor) return true;
        if (minor < other.minor) return false;
        return patch > other.patch;
    }

    public boolean isNewerThan(String s) {
        SemVer other = fromString(s);
        return isNewerThan(other);
    }

    public boolean isEqualTo(SemVer other) {
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    public boolean isEqualTo(String s) {
        SemVer other = fromString(s);
        return isEqualTo(other);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    public static class Adapter implements JsonDeserializer<SemVer> {
        @Override
        public SemVer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return SemVer.fromString(json.getAsString());
        }
    }
}
