package yesman.epicfight.api.client.animation.property;

import java.util.Locale;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class TrailInfo {
	public final Vec3 start;
	public final Vec3 end;
	public final SimpleParticleType particle;
	public final String joint;
	public final float startTime;
	public final float endTime;
	public final float fadeTime;
	public final float rCol;
	public final float gCol;
	public final float bCol;
	public final int interpolateCount;
	public final int trailLifetime;
	public final ResourceLocation texturePath;
	public final InteractionHand hand;
	
	private TrailInfo(TrailInfo.Builder builder) {
		this.start = builder.start;
		this.end = builder.end;
		this.joint = builder.joint;
		this.particle = builder.particle;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
		this.fadeTime = builder.fadeTime;
		this.rCol = builder.rCol;
		this.gCol = builder.gCol;
		this.bCol = builder.bCol;
		this.interpolateCount = builder.interpolateCount;
		this.trailLifetime = builder.trailLifetime;
		this.texturePath = builder.texturePath;
		this.hand = builder.hand;
	}
	
	public TrailInfo overwrite(TrailInfo trailInfo) {
		boolean validTime = trailInfo.startTime >= 0.0F && trailInfo.endTime >= 0.0F;
		boolean validColor = trailInfo.rCol >= 0.0F && trailInfo.gCol >= 0.0F && trailInfo.bCol >= 0.0F;
		TrailInfo.Builder builder = new TrailInfo.Builder();
		
		builder.startPos((trailInfo.start == null) ? this.start : trailInfo.start);
		builder.endPos((trailInfo.end == null) ? this.end : trailInfo.end);
		builder.joint((trailInfo.joint == null) ? this.joint : trailInfo.joint);
		builder.type((trailInfo.particle == null) ? this.particle : trailInfo.particle);
		builder.time((!validTime) ? this.startTime : trailInfo.startTime, (!validTime) ? this.endTime : trailInfo.endTime);
		builder.fadeTime((trailInfo.fadeTime < 0.0F) ? this.fadeTime : trailInfo.fadeTime);
		builder.r(!(validColor) ? this.rCol : trailInfo.rCol);
		builder.g(!(validColor) ? this.gCol : trailInfo.gCol);
		builder.b(!(validColor) ? this.bCol : trailInfo.bCol);
		builder.interpolations((trailInfo.interpolateCount < 0) ? this.interpolateCount : trailInfo.interpolateCount);
		builder.lifetime((trailInfo.trailLifetime < 0) ? this.trailLifetime : trailInfo.trailLifetime);
		builder.texture((trailInfo.texturePath == null) ? this.texturePath : trailInfo.texturePath);
		builder.itemSkinHand((trailInfo.hand == null) ? this.hand : trailInfo.hand);
		
		return builder.create();
	}
	
	public static TrailInfo.Builder builder() {
		return new TrailInfo.Builder();
	}
	
	public static TrailInfo deserialize(JsonElement json) {
		JsonObject trailObj = json.getAsJsonObject();
		TrailInfo.Builder trailBuilder = TrailInfo.builder();
		
		if (trailObj.has("start_time") && trailObj.has("end_time")) {
			float startTime = GsonHelper.getAsFloat(trailObj, "start_time");
			float endTime = GsonHelper.getAsFloat(trailObj, "end_time");
			trailBuilder.time(startTime, endTime);
		}
		
		if (trailObj.has("fade_time")) {
			float fadeTime = trailObj.get("fade_time").getAsFloat();
			trailBuilder.fadeTime(fadeTime);
		}
		
		if (trailObj.has("lifetime")) {
			trailBuilder.lifetime(GsonHelper.getAsInt(trailObj, "lifetime"));
		}
		
		if (trailObj.has("interpolations")) {
			trailBuilder.interpolations(GsonHelper.getAsInt(trailObj, "interpolations"));
		}
		
		if (trailObj.has("joint")) {
			trailBuilder.joint(GsonHelper.getAsString(trailObj, "joint"));
		}
		
		if (trailObj.has("texture_path")) {
			trailBuilder.texture(GsonHelper.getAsString(trailObj, "texture_path"));
		}
		
		if (trailObj.has("particle_type")) {
			String particleTypeName = GsonHelper.getAsString(trailObj, "particle_type");
			SimpleParticleType particleType = (SimpleParticleType)ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(particleTypeName));
			trailBuilder.type(particleType);
		}
		
		if (trailObj.has("color")) {
			JsonArray color = trailObj.get("color").getAsJsonArray();
			Vec3f colorVec = ParseUtil.toVector3f(color);
			trailBuilder.r(colorVec.x / 255F);
			trailBuilder.g(colorVec.y / 255F);
			trailBuilder.b(colorVec.z / 255F);
		}
		
		if (trailObj.has("begin_pos")) {
			JsonArray beginPos = trailObj.get("begin_pos").getAsJsonArray();
			Vec3 begin = ParseUtil.toVector3d(beginPos);
			trailBuilder.startPos(begin);
		}
		
		if (trailObj.has("end_pos")) {
			JsonArray endPos = trailObj.get("end_pos").getAsJsonArray();
			Vec3 end = ParseUtil.toVector3d(endPos);
			trailBuilder.endPos(end);
		}
		
		if (trailObj.has("item_skin_hand")) {
			String itemSkinHand = trailObj.get("item_skin_hand").getAsString();
			InteractionHand hand = InteractionHand.valueOf(itemSkinHand.toUpperCase(Locale.ROOT));
			trailBuilder.itemSkinHand(hand);
		}
		
		return trailBuilder.create();
	}
	
	public static class Builder {
		private Vec3 start;
		private Vec3 end;
		private SimpleParticleType particle;
		private String joint;
		private float startTime = -1F;
		private float endTime = -1F;
		private float fadeTime = -1F;
		private float rCol = -1F;
		private float gCol = -1F;
		private float bCol = -1F;
		private int interpolateCount = -1;
		private int trailLifetime = -1;
		private ResourceLocation texturePath;
		private InteractionHand hand = InteractionHand.MAIN_HAND;
		
		public TrailInfo.Builder startPos(Vec3 start) {
			this.start = start;
			return this;
		}
		
		public TrailInfo.Builder endPos(Vec3 end) {
			this.end = end;
			return this;
		}
		
		public TrailInfo.Builder type(SimpleParticleType particle) {
			this.particle = particle;
			return this;
		}
		
		public TrailInfo.Builder joint(String joint) {
			this.joint = joint;
			return this;
		}
		
		public TrailInfo.Builder time(float startTime, float endTime) {
			this.startTime = startTime;
			this.endTime = endTime;
			return this;
		}
		
		public TrailInfo.Builder fadeTime(float fadeTime) {
			this.fadeTime = fadeTime;
			return this;
		}
		
		public TrailInfo.Builder r(float rCol) {
			this.rCol = rCol;
			return this;
		}
		
		public TrailInfo.Builder g(float gCol) {
			this.gCol = gCol;
			return this;
		}
		
		public TrailInfo.Builder b(float bCol) {
			this.bCol = bCol;
			return this;
		}
		
		public TrailInfo.Builder interpolations(int interpolateCount) {
			this.interpolateCount = interpolateCount;
			return this;
		}
		
		public TrailInfo.Builder lifetime(int trailLifetime) {
			this.trailLifetime = trailLifetime;
			return this;
		}
		
		public TrailInfo.Builder texture(String texturePath) {
			this.texturePath = new ResourceLocation(texturePath);
			return this;
		}
		
		public TrailInfo.Builder texture(ResourceLocation texturePath) {
			this.texturePath = texturePath;
			return this;
		}
		
		public TrailInfo.Builder itemSkinHand(InteractionHand itemSkinHand) {
			this.hand = itemSkinHand;
			return this;
		}
		
		public TrailInfo create() {
			return new TrailInfo(this);
		}
	}
}