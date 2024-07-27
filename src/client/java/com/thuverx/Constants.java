package com.thuverx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thuverx.molang.Query;
import com.thuverx.molang.VectorQuery;
import com.thuverx.resource.structure.animation.AnimationJSON;
import com.thuverx.resource.structure.models.GeometryJSON;
import com.thuverx.resource.structure.support.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {

	public static final String MOD_ID = "animatedchicken";
	public static final String MOD_NAME = "Animated Chicken";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
	public static final SemVer ENGINE_VERSION = new SemVer(1, 20, 8);

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(StringOrRecord.class, new StringOrRecord.Adapter())
			.registerTypeAdapter(Vector2f.class, new Vector2fAdapter())
			.registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
			.registerTypeAdapter(GeometryUV.class, new GeometryUV.Adapter())
			.registerTypeAdapter(GeometryJSON.class, new GeometryJSON.Adapter())
			.registerTypeAdapter(SemVer.class, new SemVer.Adapter())
			.registerTypeAdapter(VectorQuery.class, new VectorQuery.Adapter())
			.registerTypeAdapter(Query.class, new Query.Adapter())
			.registerTypeAdapter(AnimationJSON.Animation.AnimatedBone.class, new AnimationJSON.Animation.AnimatedBone.Adapter())
			.registerTypeAdapter(AnimationJSON.Animation.KeyFrame.class, new AnimationJSON.Animation.KeyFrame.Adapter())
			.setPrettyPrinting()
			.setLenient()
			.create();

	public static class Identifiers {
		public static final String MINECRAFT_CLIENT_ENTITY = "minecraft:client_entity";
		public static final String MINECRAFT_GEOMETRY = "minecraft:geometry";
	}
}