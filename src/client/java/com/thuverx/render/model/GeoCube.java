package com.thuverx.render.model;

import com.thuverx.resource.structure.models.GeometryJSON;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GeoCube {

    private GeoBone parent;

    private Vector3f origin = new Vector3f();
    private Vector3f pivot = new Vector3f();
    private Vector3f size = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector2f uv = new Vector2f();
    private Vector3f inflate = new Vector3f();
    private Vector3f scale = new Vector3f(1,1,1);
    private boolean mirror = false;

    public GeoCube(GeoBone parent) {
        this.parent = parent;
    }

    public GeoCube(GeoBone parent, Vector3f origin, Vector3f pivot, Vector3f size, Vector3f rotation, Vector2f uv, Vector3f inflate,
            boolean mirror) {
        this.parent = parent;
        this.origin = origin;
        this.pivot = pivot;
        this.size = size;
        this.rotation = rotation;
        this.uv = uv;
        this.mirror = mirror;
        this.inflate = inflate;
    }


    public Vector3f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    public Vector3f getPivot() {
        return pivot;
    }

    public void setPivot(Vector3f pivot) {
        this.pivot = pivot;
    }

    public Vector3f getSize() {
        return size;
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Vector2f getUv() {
        return uv;
    }

    public void setUv(Vector2f uv) {
        this.uv = uv;
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public Vector3f getInflate() {
        return inflate;
    }

    public void setInflate(Vector3f inflate) {
        this.inflate = inflate;
        build();
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public GeoBone getParent() {
        return parent;
    }

    public static final Vector3f up = new Vector3f(0, 1, 0);
    public static final Vector3f down = new Vector3f(0, -1, 0);
    public static final Vector3f north = new Vector3f(0, 0, -1);
    public static final Vector3f south = new Vector3f(0, 0, 1);
    public static final Vector3f east = new Vector3f(1, 0, 0);
    public static final Vector3f west = new Vector3f(-1, 0, 0);

    public void build() {
        quads.clear();

        float x = origin.x;
        float y = origin.y;
        float z = origin.z;

        float u = uv.x;
        float v = uv.y;

        float sizeX = size.x;
        float sizeY = size.y;
        float sizeZ = size.z;

        float extraX = inflate.x;
        float extraY = inflate.y;
        float extraZ = inflate.z;

        float maxX = x + sizeX;
        float maxY = y + sizeY;
        float maxZ = z + sizeZ;

        x -= extraX;
        y -= extraY;
        z -= extraZ;

        maxX += extraX;
        maxY += extraY;
        maxZ += extraZ;

        if (this.parent.isMirror() || this.mirror) {
            float temp = maxX;
            maxX = x;
            x = temp;
        }

        float j = u;
        float k = u + (float)Math.floor(sizeZ);
        float l = u + (float)Math.floor(sizeZ) + (float)Math.floor(sizeX);
        float m = u + (float)Math.floor(sizeZ) + (float)Math.floor(sizeX) + (float)Math.floor(sizeX);
        float n = u + (float)Math.floor(sizeZ) + (float)Math.floor(sizeX) + (float)Math.floor(sizeZ);
        float o = u + (float)Math.floor(sizeZ) + (float)Math.floor(sizeX) + (float)Math.floor(sizeZ) + (float)Math.floor(sizeX);
        float p = v;
        float q = v + (float)Math.floor(sizeZ);
        float r = v + (float)Math.floor(sizeZ) + (float)Math.floor(sizeY);

        Vector3f vertex = new Vector3f(x, y, z);
        Vector3f vertex2 = new Vector3f(maxX, y, z);
        Vector3f vertex3 = new Vector3f(maxX, maxY, z);
        Vector3f vertex4 = new Vector3f(x, maxY, z);
        Vector3f vertex5 = new Vector3f(x, y, maxZ);
        Vector3f vertex6 = new Vector3f(maxX, y, maxZ);
        Vector3f vertex7 = new Vector3f(maxX, maxY, maxZ);
        Vector3f vertex8 = new Vector3f(x, maxY, maxZ);

        float textureWidth = parent.getModel().getTextureWidth();
        float textureHeight = parent.getModel().getTextureHeight();

        quads.add(new Quad(vertex6, vertex5, vertex, vertex2, l, q, m, p, textureWidth, textureHeight, down));
        quads.add(new Quad(vertex3, vertex4, vertex8, vertex7, k, p, l, q, textureWidth, textureHeight, up));

        quads.add(new Quad(vertex, vertex5, vertex8, vertex4, j, q, k, r, textureWidth, textureHeight, west));
        quads.add(new Quad(vertex2, vertex, vertex4, vertex3, k, q, l, r, textureWidth, textureHeight, north));
        quads.add(new Quad(vertex6, vertex2, vertex3, vertex7, l, q, n, r, textureWidth, textureHeight, east));
        quads.add(new Quad(vertex5, vertex6, vertex7, vertex8, n, q, o, r, textureWidth, textureHeight, south));
    }

    private final List<Quad> quads = new ArrayList<>();

    private record Quad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float u1, float v1, float u2, float v2,
            float textureWidth, float textureHeight, Vector3f normal) {
        public void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay) {

            Matrix4f modelMatrix = entry.getPositionMatrix();
            Vector3f vector3f = new Vector3f();

            Vector3f normal = entry.transformNormal(this.normal, vector3f);
            float normalX = normal.x();
            float normalY = normal.y();
            float normalZ = normal.z();

            {
                float x = p1.x() / 16.0F;
                float y = p1.y() / 16.0F;
                float z = p1.z() / 16.0F;

                float u = u2 / textureWidth;
                float v = v2 / textureHeight;

                Vector3f pos = modelMatrix.transformPosition(x, y, z, vector3f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), -1, u, v, overlay, light, normalX, normalY, normalZ);
            }

            {
                float x = p2.x() / 16.0F;
                float y = p2.y() / 16.0F;
                float z = p2.z() / 16.0F;

                float u = u1 / textureWidth;
                float v = v2 / textureHeight;

                Vector3f pos = modelMatrix.transformPosition(x, y, z, vector3f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), -1, u, v, overlay, light, normalX, normalY, normalZ);
            }

            {
                float x = p3.x() / 16.0F;
                float y = p3.y() / 16.0F;
                float z = p3.z() / 16.0F;

                float u = u1 / textureWidth;
                float v = v1 / textureHeight;

                Vector3f pos = modelMatrix.transformPosition(x, y, z, vector3f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), -1, u, v, overlay, light, normalX, normalY, normalZ);
            }

            {
                float x = p4.x() / 16.0F;
                float y = p4.y() / 16.0F;
                float z = p4.z() / 16.0F;

                float u = u2 / textureWidth;
                float v = v1 / textureHeight;

                Vector3f pos = modelMatrix.transformPosition(x, y, z, vector3f);
                vertexConsumer.vertex(pos.x(), pos.y(), pos.z(), -1, u, v, overlay, light, normalX, normalY, normalZ);
            }
        }
    }

    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay) {
        matrixStack.push();

        matrixStack.translate(this.pivot.x / 16.0F, this.pivot.y / 16.0F, this.pivot.z / 16.0F);
        matrixStack.multiply((new Quaternionf()).rotationZYX(this.rotation.z, this.rotation.y, this.rotation.x));
        matrixStack.scale(this.scale.x, this.scale.y, this.scale.z);
        matrixStack.translate(-this.pivot.x / 16.0F, -this.pivot.y / 16.0F, -this.pivot.z / 16.0F);

        for (Quad quad : quads) {
            quad.render(matrixStack.peek(), vertexConsumer, light, overlay);
        }

        matrixStack.pop();
    }

    public static GeoCube from(GeoBone parent, GeometryJSON.Geometry.Bone.Cube cube) {
        GeoCube geoCube = new GeoCube(parent);

        float rotX = (float) -Math.toRadians(cube.rotation.x);
        float rotY = (float) Math.toRadians(cube.rotation.y);
        float rotZ = (float) Math.toRadians(cube.rotation.z);

        geoCube.setOrigin(new Vector3f(cube.origin));
        geoCube.setPivot(new Vector3f(cube.pivot));
        geoCube.setSize(new Vector3f(cube.size));
        geoCube.setRotation(new Vector3f(rotX, rotY, rotZ));
        if(!cube.uv.isSided()) {
            geoCube.setUv(new Vector2f(cube.uv.u, cube.uv.v));
        }
        geoCube.setMirror(cube.mirror);
        geoCube.setInflate(new Vector3f((float) cube.inflate));

        return geoCube;
    }
}
