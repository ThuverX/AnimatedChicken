package com.thuverx.render.model;

import com.thuverx.resource.model.Geometry;
import com.thuverx.resource.structure.models.GeometryJSON;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public class GeoModel {
    private Map<String, GeoBone> rootBones;
    private final String identifier;
    private int textureWidth;
    private int textureHeight;
    private Map<String, GeoBone> boneCache = new HashMap<>();

    public GeoModel(String identifier) {
        this.identifier = identifier;
        this.rootBones = new HashMap<>();
    }

    public void addBone(String name, GeoBone bone) {
        rootBones.put(name, bone);
    }

    public Map<String, GeoBone> getRootBones() {
        return rootBones;
    }

    public Map<String, GeoBone> getAllBones() {
        Map<String, GeoBone> allBones = new HashMap<>();
        for (GeoBone bone : rootBones.values()) {
            allBones.put(bone.getName(), bone);
            allBones.putAll(bone.getChildren());
        }
        return allBones;
    }

    public GeoBone getRootBone(String name) {
        return rootBones.get(name);
    }

    public GeoBone getBone(String name) {
        if(boneCache.containsKey(name)) {
            return boneCache.get(name);
        }

        if(rootBones.containsKey(name)) {
            boneCache.put(name, rootBones.get(name));
            return rootBones.get(name);
        }

        for (GeoBone bone : rootBones.values()) {
            GeoBone found = bone.getChild(name);
            if (found != null) {
                boneCache.put(name, found);
                return found;
            }
        }
        return null;
    }

    public void setRootBones(Map<String, GeoBone> rootBones) {
        this.rootBones = rootBones;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public void setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public void setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        matrixStack.push();
        matrixStack.scale(-1,1,1);

        for (GeoBone bone : rootBones.values()) {
            bone.render(matrixStack, vertexConsumer, light, overlay);
        }
        matrixStack.pop();
    }

    public static GeoModel of(Geometry geometry) {
        // this is fucking dumb but fits the code better
        GeometryJSON.Geometry json = geometry.json();
        GeoModel geom = new GeoModel(json.description.identifier);

        geom.setTextureWidth(json.description.texture_width);
        geom.setTextureHeight(json.description.texture_height);

        HashMap<String, GeoBone> rootBones = new HashMap<>();

        for (GeometryJSON.Geometry.Bone bone : json.bones) {
            if (bone.parent == null) {
                GeoBone geoBone = GeoBone.from(geom, bone);
                rootBones.put(bone.name, geoBone);
                geom.addBone(bone.name, geoBone);
                geom.addChildren(geoBone, json);
            }
        }

        geom.setRootBones(rootBones);

        return geom;
    }

    private void addChildren(GeoBone parent, GeometryJSON.Geometry geometry) {
        for (GeometryJSON.Geometry.Bone bone : geometry.bones) {
            if (bone.parent != null && bone.parent.equals(parent.getName())) {
                GeoBone geoBone = GeoBone.from(this, bone);
                geoBone.setParent(parent);
                parent.addChild(geoBone);
                addChildren(geoBone, geometry);
            }
        }
    }

    public void resetAnimation() {
        for (GeoBone bone : getRootBones().values()) {
            bone.resetAnimation();
        }
    }
}
