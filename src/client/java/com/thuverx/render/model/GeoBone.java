package com.thuverx.render.model;

import com.thuverx.resource.structure.models.GeometryJSON;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeoBone {
    private GeoBone parent;
    private GeoModel model;

    private String name;
    private boolean mirror;
    private Vector3f pivot = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Map<String, GeoBone> children = new HashMap<>();
    private List<GeoCube> cubes = new ArrayList<>();

    public GeoBone(GeoModel model) {
        this.model = model;
    }

    public GeoBone(GeoModel model, GeoBone parent) {
        this.parent = parent;
        this.model = model;
    }

    public GeoBone(GeoModel model, GeoBone parent, String name, boolean mirror,Vector3f rotation, Vector3f pivot, Map<String, GeoBone> children, List<GeoCube> cubes) {
        this.parent = parent;
        this.model = model;
        this.name = name;
        this.mirror = mirror;
        this.children = children;
        this.cubes = cubes;
        this.pivot = pivot;
        this.rotation = rotation;
    }

    public void addCube(GeoCube cube) {
        cube.build();
        cubes.add(cube);
    }

    public void addChild(GeoBone bone) {
        children.put(bone.getName(), bone);
    }

    public GeoBone getChild(String name) {
        if(children.containsKey(name)) return children.get(name);
        for (GeoBone bone : children.values()) {
            GeoBone found = bone.getChild(name);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void addChild(String name, GeoBone bone) {
        children.put(name, bone);
    }

    public String getName() {
        return name;
    }
    public String getPath() {
        if(parent == null) return name;
        return parent.getPath() + "." + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public Map<String, GeoBone> getChildren() {
        return children;
    }

    public void setChildren(Map<String, GeoBone> children) {
        this.children = children;
    }

    public List<GeoCube> getCubes() {
        return cubes;
    }

    public void setCubes(List<GeoCube> cubes) {
        this.cubes = cubes;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setPitch(float pitch) {
        this.rotation.x = pitch;
    }

    public void setYaw(float yaw) {
        this.rotation.y = yaw;
    }

    public void setRoll(float roll) {
        this.rotation.z = roll;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public void setPivot(Vector3f pivot) {
        this.pivot = pivot;
    }

    public GeoBone getParent() {
        return parent;
    }
    public void setParent(GeoBone parent) {
        this.parent = parent;
    }

    public GeoModel getModel() {
        return model;
    }

    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        matrixStack.push();

        // model space
        matrixStack.translate(this.animPosition.x / 16.0F, this.animPosition.y / 16.0F, this.animPosition.z / 16.0F);

        // enter pivot space
        matrixStack.translate(this.pivot.x / 16.0F, this.pivot.y / 16.0F, this.pivot.z / 16.0F);

        matrixStack.multiply((new Quaternionf()).rotationZYX(this.rotation.z, this.rotation.y, this.rotation.x));

        matrixStack.multiply((new Quaternionf()).rotationZYX(this.animRotation.z, this.animRotation.y, this.animRotation.x));
        matrixStack.scale(this.animScale.x, this.animScale.y, this.animScale.z);

        // leave pivot space
        matrixStack.translate(-this.pivot.x / 16.0F, -this.pivot.y / 16.0F, -this.pivot.z / 16.0F);

        for (GeoCube cube : cubes) {
            cube.render(matrixStack, vertexConsumer, light, overlay);
        }

        for (GeoBone bone : children.values()) {
            bone.render(matrixStack, vertexConsumer, light, overlay);
        }

        matrixStack.pop();
    }

    public static GeoBone from(GeoModel geom, GeometryJSON.Geometry.Bone bone) {
        GeoBone geoBone = new GeoBone(geom);

        float rotX = (float) -Math.toRadians(bone.rotation.x);
        float rotY = (float) Math.toRadians(bone.rotation.y);
        float rotZ = (float) Math.toRadians(bone.rotation.z);

        geoBone.setName(bone.name);
        geoBone.setMirror(bone.mirror);
        geoBone.setPivot(new Vector3f(bone.pivot));
        geoBone.setRotation(new Vector3f(rotX, rotY, rotZ));

        for (GeometryJSON.Geometry.Bone.Cube cube : bone.cubes) {
            GeoCube geoCube = GeoCube.from(geoBone, cube);
            geoBone.addCube(geoCube);
        }

        return geoBone;
    }

    private Vector3f animPosition = new Vector3f();
    private Vector3f animRotation = new Vector3f();
    private Vector3f animScale = new Vector3f(1, 1, 1);

    public void setAnimPosition(Vector3f vector3f) {
        this.animPosition = vector3f;
    }

    public void setAnimRotation(Vector3f vector3f) {
        this.animRotation = vector3f;
    }

    public void setAnimScale(Vector3f vector3f) {
        this.animScale = vector3f;
    }

    public void addAnimPosition(Vector3f vector3f) {
        this.animPosition.add(vector3f);
    }

    public void addAnimRotation(Vector3f vector3f) {
        this.animRotation.add(vector3f);
    }

    public void addAnimScale(Vector3f vector3f) {
        this.animScale.add(vector3f);
    }

    public void resetAnimation() {
        this.animPosition = new Vector3f();
        this.animRotation = new Vector3f();
        this.animScale = new Vector3f(1, 1, 1);

        for(GeoBone bone : children.values()) {
            bone.resetAnimation();
        }
    }
}
