package com.thuverx.resource.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManifestJSON {
    public static class Header {
        public String description;
        public String name;
        public UUID uuid;
        public int[] version;
        public int[] min_engine_version;
    }

    public static class Module {
        public String description;
        public String type;
        public UUID uuid;
        public int[] version;
    }
    public int format_version;
    public Header header;
    public List<Module> modules = new ArrayList<>();
}
