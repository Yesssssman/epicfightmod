package yesman.epicfight.api.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.main.EpicFightMod;

public class JsonModelLoader {
	private static final OpenMatrix4f CORRECTION = OpenMatrix4f.createRotatorDeg(-90.0F, Vec3f.X_AXIS);
	
	private static int[] toIntArray(JsonArray array) {
		List<Integer> result = Lists.newArrayList();
		for (JsonElement je : array) {
			result.add(je.getAsInt());
		}
		
		return ArrayUtils.toPrimitive(result.toArray(new Integer[0]));
	}
	
	private static float[] toFloatArray(JsonArray array) {
		List<Float> result = Lists.newArrayList();
		for (JsonElement je : array) {
			result.add(je.getAsFloat());
		}
		
		return ArrayUtils.toPrimitive(result.toArray(new Float[0]));
	}
	
	private JsonObject rootJson;
	private int jointIdCount;
	
	public JsonModelLoader(ResourceManager resourceManager, ResourceLocation resourceLocation) {
		if (resourceManager == null) {
			BufferedInputStream inputstream = new BufferedInputStream(EpicFightMod.class.getResourceAsStream("/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()));
			Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
			JsonReader in = new JsonReader(reader);
			in.setLenient(true);
			this.rootJson = Streams.parse(in).getAsJsonObject();	
		} else {
			if (resourceManager.hasResource(resourceLocation)) {
				try {
					Resource resource = resourceManager.getResource(resourceLocation);
					InputStream inputstream = resource.getInputStream();
					Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
					JsonReader in = new JsonReader(reader);
					in.setLenient(true);
					this.rootJson = Streams.parse(in).getAsJsonObject();	
				} catch (IOException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("[EpicFightMod] : get an exception reading " + resourceLocation.toString());
				}
			} else {
				throw new IllegalArgumentException("[EpicFightMod] : no model file in " + resourceLocation.toString());
			}
		}
	}
	
	public Mesh getMesh() {
		JsonObject obj = this.rootJson.getAsJsonObject("vertices");
		JsonObject positions = obj.getAsJsonObject("positions");
		JsonObject normals = obj.getAsJsonObject("normals");
		JsonObject uvs = obj.getAsJsonObject("uvs");
		JsonObject vdincies = obj.getAsJsonObject("vindices");
		JsonObject weights = obj.getAsJsonObject("weights");
		JsonObject drawingIndices = obj.getAsJsonObject("indices");
		JsonObject vcounts = obj.getAsJsonObject("vcounts");
		
		float[] positionArray = toFloatArray(positions.get("array").getAsJsonArray());
		for (int i = 0; i < positionArray.length / 3; i++) {
			int k = i * 3;
			Vec4f posVector = new Vec4f(positionArray[k], positionArray[k+1], positionArray[k+2], 1.0F);
			OpenMatrix4f.transform(CORRECTION, posVector, posVector);
			positionArray[k] = posVector.x;
			positionArray[k+1] = posVector.y;
			positionArray[k+2] = posVector.z;
		}
		
		float[] normalArray = toFloatArray(normals.get("array").getAsJsonArray());
		for (int i = 0; i < normalArray.length / 3; i++) {
			int k = i * 3;
			Vec4f normVector = new Vec4f(normalArray[k], normalArray[k+1], normalArray[k+2], 1.0F);
			OpenMatrix4f.transform(CORRECTION, normVector, normVector);
			normalArray[k] = normVector.x;
			normalArray[k+1] = normVector.y;
			normalArray[k+2] = normVector.z;
		}
		
		float[] uvArray = toFloatArray(uvs.get("array").getAsJsonArray());
		int[] vindexArray = toIntArray(vdincies.get("array").getAsJsonArray());
		float[] weightArray = toFloatArray(weights.get("array").getAsJsonArray());
		int[] drawingIndexArray = toIntArray(drawingIndices.get("array").getAsJsonArray());
		int[] vcountArray = toIntArray(vcounts.get("array").getAsJsonArray());
		
		return new Mesh(positionArray, normalArray, uvArray, vindexArray, weightArray, drawingIndexArray, vcountArray);
	}
	
	public Armature getArmature() {
		JsonObject obj = this.rootJson.getAsJsonObject("armature");
		JsonObject hierarchy = obj.get("hierarchy").getAsJsonArray().get(0).getAsJsonObject();
		Map<String, Joint> jointMap = Maps.newHashMap();
		Joint joint = this.getJoint(hierarchy, jointMap, true);
		joint.setInversedModelTransform(new OpenMatrix4f());
		return new Armature(jointMap.size(), joint, jointMap);
	}
	
	public Joint getJoint(JsonObject object, Map<String, Joint> jointMap, boolean start) {
		float[] floatArray = toFloatArray(object.get("transform").getAsJsonArray());
		OpenMatrix4f localMatrix = new OpenMatrix4f().load(FloatBuffer.wrap(floatArray));
		localMatrix.transpose();
		if (start) {
			localMatrix.mulFront(CORRECTION);
		}
		
		String name = object.get("name").getAsString();
		Joint joint = new Joint(name, this.jointIdCount, localMatrix);
		jointMap.put(name, joint);
		this.jointIdCount++;
		
		for (JsonElement children : object.get("children").getAsJsonArray()) {
			joint.addSubJoint(this.getJoint(children.getAsJsonObject(), jointMap, false));
		}
		
		return joint;
	}
	
	public void loadStaticAnimation(StaticAnimation animation) {
		JsonArray array = this.rootJson.get("animation").getAsJsonArray();
		boolean action = animation instanceof ActionAnimation;
		boolean attack = animation instanceof AttackAnimation;
		boolean root = true;
		
		if (!action && !attack) {
			if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
				return;
			}
		}
		
		List<String> allowedJointList = Lists.<String>newArrayList();
		
		if (attack) {
			for (Phase phase : ((AttackAnimation)animation).phases) {
				Armature armature = animation.getModel().getArmature();
				Joint joint = armature.getJointHierarcy();
				int pathIndex = armature.searchPathIndex(phase.getColliderJointName());
				
				while (joint != null) {
					allowedJointList.add(joint.getName());
					int nextJoint = pathIndex % 10;
					
					if (nextJoint > 0) {
						pathIndex /= 10;
						joint = joint.getSubJoints().get(nextJoint - 1);
					} else {
						joint = null;
					}
				}
			}
		}
		
		for (JsonElement element : array) {
			JsonObject keyObject = element.getAsJsonObject();
			String name = keyObject.get("name").getAsString();
			
			if (attack && FMLEnvironment.dist == Dist.DEDICATED_SERVER && !allowedJointList.contains(name)) {
				continue;
			}
			
			Joint joint = animation.getModel().getArmature().searchJointByName(name);
			
			if (joint == null) {
				throw new IllegalArgumentException("[EpicFightMod] Can't find the joint " + name + " in animation data " + animation);
			}
			
			JsonArray timeArray = keyObject.getAsJsonArray("time");
			JsonArray transformArray = keyObject.getAsJsonArray("transform");
			int timeNum = timeArray.size();
			int matrixNum = transformArray.size();
			float[] times = new float[timeNum];
			float[] transforms = new float[matrixNum * 16];
			
			for (int i = 0; i < timeNum; i++) {
				times[i] = timeArray.get(i).getAsFloat();
			}
			
			for (int i = 0; i < matrixNum; i++) {
				JsonArray matrixJson = transformArray.get(i).getAsJsonArray();
				for (int j = 0; j < 16; j++) {
					transforms[i * 16 + j] = matrixJson.get(j).getAsFloat();
				}
			}
			
			TransformSheet sheet = getTransformSheet(times, transforms, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			animation.addSheet(name, sheet);
			animation.setTotalTime(times[times.length - 1]);
			root = false;
			
			if (action) {
				if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
					break;
				}
			}
		}
	}
	
	public void loadStaticAnimationBothSide(StaticAnimation animation) {
		JsonArray array = this.rootJson.get("animation").getAsJsonArray();
		boolean root = true;
		
		for (JsonElement element : array) {
			JsonObject keyObject = element.getAsJsonObject();
			String name = keyObject.get("name").getAsString();
			Joint joint = animation.getModel().getArmature().searchJointByName(name);
			
			if (joint == null) {
				throw new IllegalArgumentException("[EpicFightMod] Can't find the joint " + name + " in animation data " + animation);
			}
			
			JsonArray timeArray = keyObject.getAsJsonArray("time");
			JsonArray transformArray = keyObject.getAsJsonArray("transform");
			int timeNum = timeArray.size();
			int matrixNum = transformArray.size();
			float[] times = new float[timeNum];
			float[] transforms = new float[matrixNum * 16];
			
			for (int i = 0; i < timeNum; i++) {
				times[i] = timeArray.get(i).getAsFloat();
			}
			
			for (int i = 0; i < matrixNum; i++) {
				JsonArray matrixJson = transformArray.get(i).getAsJsonArray();
				for (int j = 0; j < 16; j++) {
					transforms[i * 16 + j] = matrixJson.get(j).getAsFloat();
				}
			}
			
			TransformSheet sheet = getTransformSheet(times, transforms, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			animation.addSheet(name, sheet);
			animation.setTotalTime(times[times.length - 1]);
			root = false;
		}
	}
	
	private static TransformSheet getTransformSheet(float[] times, float[] trasnformMatrix, OpenMatrix4f invLocalTransform, boolean correct) {
		List<Keyframe> keyframeList = new ArrayList<Keyframe> ();
		
		for (int i = 0; i < times.length; i++) {
			float timeStamp = times[i];
			if (timeStamp < 0) {
				continue;
			}
			
			float[] matrixElements = new float[16];
			for (int j = 0; j < 16; j++) {
				matrixElements[j] = trasnformMatrix[i*16 + j];
			}
			
			OpenMatrix4f matrix = new OpenMatrix4f().load(FloatBuffer.wrap(matrixElements));
			matrix.transpose();
			
			if (correct) {
				matrix.mulFront(CORRECTION);
			}
			
			matrix.mulFront(invLocalTransform);
			
			JointTransform transform = new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), matrix.toScaleVector());
			keyframeList.add(new Keyframe(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		return sheet;
	}
}