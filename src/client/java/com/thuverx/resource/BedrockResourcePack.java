package com.thuverx.resource;

import com.thuverx.Constants;
import com.thuverx.events.BedrockPacksAppliedCallback;
import com.thuverx.events.BedrockPacksScannedCallback;
import com.thuverx.resource.entity.ClientEntity;
import com.thuverx.render.model.GeoModel;
import com.thuverx.resource.model.Geometry;
import com.thuverx.resource.structure.ManifestJSON;
import com.thuverx.resource.structure.SplashesJSON;
import com.thuverx.resource.structure.animation.AnimationJSON;
import com.thuverx.resource.structure.animation_controllers.AnimationControllerJSON;
import com.thuverx.resource.structure.entity.EntityJSON;
import com.thuverx.resource.structure.models.GeometryJSON;
import com.thuverx.util.FolderPackExtractor;
import com.thuverx.util.PackExtractor;
import com.thuverx.util.ZipPackExtractor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BedrockResourcePack {


    public enum DistributionType {
        FOLDER,
        MCPACK
    }

    public enum PackModule {
        ANIMATION_CONTROLLERS("animation_controllers", "**.json", AnimationControllerJSON.class),
        ANIMATIONS("animations", "**.json", AnimationJSON.class),
        ATTACHABLES("attachables", "**.json", null),
        CAMERAS("cameras", "**.json", null),
        ENTITIES("entity", "**.json", EntityJSON.class),
        FOGS("fogs", "**.json", null),
        MATERIALS("materials", "**.json", null),
        FONTS("font", "**.json", null),
        MODELS("models", "**.json", GeometryJSON.class),
        PARTICLES("particles", "**.json", null),
        RENDER_CONTROLLERS("render_controllers", "**.json", null),
        SOUNDS("sounds", "**.json", null),

        //TODO: Add .lang support
        TEXTS("texts", "**.json", null),
        TEXTURES("textures", "**.png", null),
        UI("ui", "**.json", null),
        MANIFEST("", "manifest.json", ManifestJSON.class),
        SPLASHES("", "splashes.json", SplashesJSON.class),
        BIOMES_CLIENT_LIST("", "biomes_client.json", null),
        BLOCKS_LIST("", "blocks.json", null),
        SOUNDS_LIST("", "sounds.json", null),
        ;

        private final String folder;
        private final String wildcard;
        private final Class<?> clazz;
        PackModule(String folder, String wildcard, Class<?> clazz) {
            this.folder = folder;
            this.wildcard = wildcard;
            this.clazz = clazz;
        }

        public String getFolder() {
            return folder;
        }

        public String getWildcard() {
            return wildcard;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public String getSearchPattern() {
            if(folder.isEmpty()) return wildcard;
            return folder + "/" + wildcard;
        }
    }

    public static final File BEDROCK_PACK_FOLDER = new File("brpacks");
    public static List<BedrockResourcePack> resourcePacks = new ArrayList<>();
    private static Logger LOG = LoggerFactory.getLogger(BedrockResourcePack.class);


    // json data
    private List<EntityJSON> entities = new ArrayList<>();
    private List<AnimationJSON> animations = new ArrayList<>();
    private List<AnimationControllerJSON> animationControllers = new ArrayList<>();
    private List<GeometryJSON> geometries = new ArrayList<>();
    private SplashesJSON splashes = null;
    private ManifestJSON manifest = null;
    private List<Path> textures = new ArrayList<>();

    // unload data
    private final List<Identifier> loadedTextures = new ArrayList<>();


    private UUID uuid;
    private Text name;
    private Text description;
    private final Path path;
    private final DistributionType distributionType;
    private Identifier image;
    public BedrockResourcePack(Path path, DistributionType distributionType) {
        this.path = path;
        this.distributionType = distributionType;
        this.name = Text.of(path.getFileName().toString());
    }

    public Text getName() {
        return name;
    }

    public Text getDescription() {
        return description;
    }

    public Path getPath() {
        return path;
    }

    public Identifier getImage() {
        return image;
    }
    public Identifier getUuid() {
        return Identifier.of(uuid.toString(), "");
    }
    public Identifier getNamespace() {
        return getUuid();
    }

    // take the loaded data and apply it into the registries
    // keep in mind the order of applying based on the dependencies
    public void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        TextureManager tm = client.getTextureManager();

        for(Path texture : textures) {
            String path = getRelativePath(texture);
            Identifier id = getNamespace().withPath(path);

            try (NativeImage image = NativeImage.read(Files.readAllBytes(texture))) {
                loadedTextures.add(id);
                tm.registerTexture(id, new NativeImageBackedTexture(image));

                BedrockResourcePackRegistries.TEXTURE_REGISTRY.put(path, id);
            } catch (IOException e) {
                Constants.LOG.error("Failed to load texture {}", id);
                Constants.LOG.error(e.getMessage());
            }
        }

        for(GeometryJSON file : geometries) {
            for(GeometryJSON.Geometry geometry : file.geometries) {
                Geometry geom = new Geometry(geometry, geometry.description.identifier);
                BedrockResourcePackRegistries.GEOMETRY_REGISTRY.put(geom.identifier(), geom);
            }
        }

        for (EntityJSON entity : entities) {
            ClientEntity clientEntity = ClientEntity.of(entity);
            if(clientEntity == null) continue;
            BedrockResourcePackRegistries.CLIENT_ENTITY_REGISTRY.put(clientEntity.getIdentifier(), clientEntity);
        }

        BedrockResourcePackRegistries.SPLASHES_REGISTRY.addAll(splashes.splashes);

        cleanup();
    }

    // TODO: Figure out if this is actually needed
    //       its only meant to decrease the memory usage
    private void cleanup() {
        entities.clear();
        animations.clear();
        animationControllers.clear();
        geometries.clear();
        textures.clear();
        splashes = null;
        manifest = null;
    }

    private String getRelativePath(Path localPath) {
        String relativePath = localPath.toString();

        if(!localPath.toString().startsWith("/")) {
            relativePath = path.toUri().relativize(localPath.toUri()).getPath();
        }

        relativePath = relativePath.substring(0, relativePath.lastIndexOf('.'));

        if(relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        return relativePath;
    }

    public void load() {
        PackExtractor extractor = distributionType == DistributionType.FOLDER
                ? new FolderPackExtractor(path)
                : new ZipPackExtractor(path);

        manifest = extractor.getParsedJson(PackModule.MANIFEST.getSearchPattern(), ManifestJSON.class);

        if (manifest == null) throw new RuntimeException("Manifest not found in pack " + name.getString());

        uuid = manifest.header.uuid;
        name = Text.of(manifest.header.name);
        description = Text.of(manifest.header.description);
        image = getNamespace().withPath("pack_icon");

        entities = extractor.getParsedJsons(PackModule.ENTITIES.getSearchPattern(), EntityJSON.class);
        animations = extractor.getParsedJsons(PackModule.ANIMATIONS.getSearchPattern(), AnimationJSON.class);
        animationControllers = extractor.getParsedJsons(PackModule.ANIMATION_CONTROLLERS.getSearchPattern(), AnimationControllerJSON.class);
        geometries = extractor.getParsedJsons(PackModule.MODELS.getSearchPattern(), GeometryJSON.class);
        splashes = extractor.getParsedJson(PackModule.SPLASHES.getSearchPattern(), SplashesJSON.class);

        textures = extractor.getFiles(PackModule.TEXTURES.getSearchPattern());

        textures.add(extractor.getFile("pack_icon.png"));

        LOG.info("Loaded pack {}", name.getString());
        LOG.info("Debug info:");
        LOG.info("Entities: {}", entities.size());
        LOG.info("Animations: {}", animations.size());
        LOG.info("Animation Controllers: {}", animationControllers.size());
        LOG.info("Geometries: {}", geometries.size());
        LOG.info("Textures: {}", textures.size());
    }

    public void unload() {
        MinecraftClient client = MinecraftClient.getInstance();
        TextureManager tm = client.getTextureManager();
        for(Identifier id : loadedTextures) {
            tm.destroyTexture(id);
        }

        loadedTextures.clear();
    }

    public static void unloadAll() {
        resourcePacks.forEach(BedrockResourcePack::unload);
    }

    public static void scan() {
        resourcePacks.clear();
        File[] files = BEDROCK_PACK_FOLDER.listFiles();
        if (files == null) return;

        for (File file : files) {
            if(file.isDirectory()) {
                File manifest = new File(file, "manifest.json");
                if(!manifest.exists()) continue;

                resourcePacks.add(new BedrockResourcePack(file.toPath(), DistributionType.FOLDER));
            } else {
                if(!file.getName().endsWith(".mcpack") && !file.getName().endsWith(".zip")) continue;
                resourcePacks.add(new BedrockResourcePack(file.toPath(), DistributionType.MCPACK));
            }
        }

        String joinedNames = resourcePacks.stream()
                .map(BedrockResourcePack::getName)
                .map(Text::getString)
                .collect(Collectors.joining(", "));

        LOG.info("Found {} packs {}", resourcePacks.size(), joinedNames);

        BedrockPacksScannedCallback.EVENT.invoker().onPacksScanned(resourcePacks);
    }

    public static void loadAll() {
        resourcePacks.forEach(BedrockResourcePack::load);
    }

    public static void applyAll() {
        resourcePacks.forEach(BedrockResourcePack::apply);

        BedrockPacksAppliedCallback.EVENT.invoker().onPacksApplied();
    }
}
