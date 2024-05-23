package yesman.epicfight.api.model;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Mesh.RawMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.Meshes.MeshContructor;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Armatures.ArmatureContructor;
import yesman.epicfight.main.EpicFightMod;

public class JsonModelLoader {
	public static final OpenMatrix4f Z_AXIS_TO_Y = OpenMatrix4f.createRotatorDeg(-90.0F, Vec3f.X_AXIS);
	
	private JsonObject rootJson;
	private ResourceManager resourceManager;
	private ResourceLocation resourceLocation;
	
	public JsonModelLoader(ResourceManager resourceManager, ResourceLocation resourceLocation) throws IllegalStateException {
		JsonReader jsonReader = null;
		this.resourceManager = resourceManager;
		this.resourceLocation = resourceLocation;
		
		try {
			try {
				Resource resource = resourceManager.getResource(resourceLocation).orElseThrow();
				jsonReader = new JsonReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8));
				jsonReader.setLenient(true);
				this.rootJson = Streams.parse(jsonReader).getAsJsonObject();
			} catch (NoSuchElementException e) {
				// In this case, reads the animation data from mod.jar (Especially in a server)
				Class<?> modClass = ModList.get().getModObjectById(resourceLocation.getNamespace()).get().getClass();
				BufferedInputStream inputstream = new BufferedInputStream(modClass.getResourceAsStream("/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()));
				Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
				jsonReader = new JsonReader(reader);
				jsonReader.setLenient(true);
				this.rootJson = Streams.parse(jsonReader).getAsJsonObject();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Can't read " + resourceLocation.toString() + " because of " + e);
		} finally {
			if (jsonReader != null) {
				try {
					jsonReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public JsonModelLoader(InputStream inputstream, ResourceLocation resourceLocation) throws IOException {
		JsonReader jsonReader = null;
		this.resourceManager = Minecraft.getInstance().getResourceManager();
		this.resourceLocation = resourceLocation;
		
		jsonReader = new JsonReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
		jsonReader.setLenient(true);
		this.rootJson = Streams.parse(jsonReader).getAsJsonObject();
		jsonReader.close();
	}
	
	@OnlyIn(Dist.CLIENT)
	public JsonModelLoader(JsonObject rootJson) throws IOException {
		this.resourceManager = Minecraft.getInstance().getResourceManager();
		this.rootJson = rootJson;
	}
	
	@OnlyIn(Dist.CLIENT)
	public AnimatedMesh.RenderProperties getRenderProperties() {
		JsonObject properties = this.rootJson.getAsJsonObject("render_properties");
		AnimatedMesh.RenderProperties renderProperties = AnimatedMesh.RenderProperties.create();
		
		if (properties != null) {
			if (properties.has("transparent")) {
				renderProperties.transparency(properties.get("transparent").getAsBoolean());
			}
			
			if (properties.has("texture_path")) {
				renderProperties.customTexturePath(properties.get("texture_path").getAsString());
			}
			
			if (properties.has("parent_part_visualizer")) {
				JsonObject partVisualizer = properties.get("parent_part_visualizer").getAsJsonObject();
				
				partVisualizer.entrySet().forEach((entry) -> renderProperties.newPartVisualizer(entry.getKey(), entry.getValue().getAsBoolean()));
			}
			
			return renderProperties;
		}
		
		return renderProperties;
	}
	
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getParent() {
		return this.rootJson.has("parent") ? new ResourceLocation(this.rootJson.get("parent").getAsString()) : null;
	}
	
	@OnlyIn(Dist.CLIENT)
	public <T extends RawMesh> T loadMesh(MeshContructor<VertexIndicator, T> constructor) {
		ResourceLocation parent = this.getParent();
		
		if (parent != null) {
			T mesh = Meshes.getOrCreateRawMesh(this.resourceManager, parent, constructor);			
			return constructor.invoke(null, mesh, this.getRenderProperties(), null);
		} else {
			JsonObject obj = this.rootJson.getAsJsonObject("vertices");
			JsonObject positions = obj.getAsJsonObject("positions");
			JsonObject normals = obj.getAsJsonObject("normals");
			JsonObject uvs = obj.getAsJsonObject("uvs");
			JsonObject parts = obj.getAsJsonObject("parts");
			JsonObject indices = obj.getAsJsonObject("indices");
			
			float[] positionArray = ParseUtil.toFloatArray(positions.get("array").getAsJsonArray());
			
			for (int i = 0; i < positionArray.length / 3; i++) {
				int k = i * 3;
				Vec4f posVector = new Vec4f(positionArray[k], positionArray[k+1], positionArray[k+2], 1.0F);
				OpenMatrix4f.transform(Z_AXIS_TO_Y, posVector, posVector);
				positionArray[k] = posVector.x;
				positionArray[k+1] = posVector.y;
				positionArray[k+2] = posVector.z;
			}
			
			float[] normalArray = ParseUtil.toFloatArray(normals.get("array").getAsJsonArray());
			
			for (int i = 0; i < normalArray.length / 3; i++) {
				int k = i * 3;
				Vec4f normVector = new Vec4f(normalArray[k], normalArray[k+1], normalArray[k+2], 1.0F);
				OpenMatrix4f.transform(Z_AXIS_TO_Y, normVector, normVector);
				normalArray[k] = normVector.x;
				normalArray[k+1] = normVector.y;
				normalArray[k+2] = normVector.z;
			}
			
			float[] uvArray = ParseUtil.toFloatArray(uvs.get("array").getAsJsonArray());
			
			Map<String, float[]> arrayMap = Maps.newHashMap();
			Map<String, ModelPart<VertexIndicator>> meshMap = Maps.newHashMap();
			
			arrayMap.put("positions", positionArray);
			arrayMap.put("normals", normalArray);
			arrayMap.put("uvs", uvArray);
			
			if (parts != null) {
				for (Map.Entry<String, JsonElement> e : parts.entrySet()) {
					meshMap.put(e.getKey(), new ModelPart<>(VertexIndicator.create(ParseUtil.toIntArray(e.getValue().getAsJsonObject().get("array").getAsJsonArray()))));
				}
			}
			
			if (indices != null) {
				meshMap.put("noGroups", new ModelPart<>(VertexIndicator.create(ParseUtil.toIntArray(indices.get("array").getAsJsonArray()))));
			}
			
			return constructor.invoke(arrayMap, null, this.getRenderProperties(), meshMap);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public <T extends AnimatedMesh> T loadAnimatedMesh(MeshContructor<AnimatedVertexIndicator, T> constructor) {
		ResourceLocation parent = this.getParent();
		
		if (parent != null) {
			T mesh = Meshes.getOrCreateAnimatedMesh(this.resourceManager, parent, constructor);			
			return constructor.invoke(null, mesh, this.getRenderProperties(), null);
		} else {
			JsonObject obj = this.rootJson.getAsJsonObject("vertices");
			JsonObject positions = obj.getAsJsonObject("positions");
			JsonObject normals = obj.getAsJsonObject("normals");
			JsonObject uvs = obj.getAsJsonObject("uvs");
			JsonObject vdincies = obj.getAsJsonObject("vindices");
			JsonObject weights = obj.getAsJsonObject("weights");
			JsonObject vcounts = obj.getAsJsonObject("vcounts");
			JsonObject parts = obj.getAsJsonObject("parts");
			JsonObject indices = obj.getAsJsonObject("indices");
			
			float[] positionArray = ParseUtil.toFloatArray(positions.get("array").getAsJsonArray());
			
			for (int i = 0; i < positionArray.length / 3; i++) {
				int k = i * 3;
				Vec4f posVector = new Vec4f(positionArray[k], positionArray[k+1], positionArray[k+2], 1.0F);
				OpenMatrix4f.transform(Z_AXIS_TO_Y, posVector, posVector);
				positionArray[k] = posVector.x;
				positionArray[k+1] = posVector.y;
				positionArray[k+2] = posVector.z;
			}
			
			float[] normalArray = ParseUtil.toFloatArray(normals.get("array").getAsJsonArray());
			
			for (int i = 0; i < normalArray.length / 3; i++) {
				int k = i * 3;
				Vec4f normVector = new Vec4f(normalArray[k], normalArray[k+1], normalArray[k+2], 1.0F);
				OpenMatrix4f.transform(Z_AXIS_TO_Y, normVector, normVector);
				normalArray[k] = normVector.x;
				normalArray[k+1] = normVector.y;
				normalArray[k+2] = normVector.z;
			}
			
			float[] uvArray = ParseUtil.toFloatArray(uvs.get("array").getAsJsonArray());
			int[] animationIndexArray = ParseUtil.toIntArray(vdincies.get("array").getAsJsonArray());
			float[] weightArray = ParseUtil.toFloatArray(weights.get("array").getAsJsonArray());
			int[] vcountArray = ParseUtil.toIntArray(vcounts.get("array").getAsJsonArray());
			
			Map<String, float[]> arrayMap = Maps.newHashMap();
			Map<String, ModelPart<AnimatedVertexIndicator>> meshMap = Maps.newHashMap();
			
			arrayMap.put("positions", positionArray);
			arrayMap.put("normals", normalArray);
			arrayMap.put("uvs", uvArray);
			arrayMap.put("weights", weightArray);
			
			if (parts != null) {
				for (Map.Entry<String, JsonElement> e : parts.entrySet()) {
					meshMap.put(e.getKey(), new ModelPart<>(VertexIndicator.createAnimated(ParseUtil.toIntArray(e.getValue().getAsJsonObject().get("array").getAsJsonArray()), vcountArray, animationIndexArray)));
				}
			}
			
			if (indices != null) {
				meshMap.put("noGroups", new ModelPart<>(VertexIndicator.createAnimated(ParseUtil.toIntArray(indices.get("array").getAsJsonArray()), vcountArray, animationIndexArray)));
			}
			
			return constructor.invoke(arrayMap, null, this.getRenderProperties(), meshMap);
		}
	}
	
	public <T extends Armature> T loadArmature(ArmatureContructor<T> constructor) {
		JsonObject obj = this.rootJson.getAsJsonObject("armature");
		JsonObject hierarchy = obj.get("hierarchy").getAsJsonArray().get(0).getAsJsonObject();
		JsonArray nameAsVertexGroups = obj.getAsJsonArray("joints");
		Map<String, Joint> jointMap = Maps.newHashMap();
		Joint joint = this.getJoint(hierarchy, nameAsVertexGroups, jointMap, true);
		joint.initOriginTransform(new OpenMatrix4f());
		String armatureName = this.resourceLocation.toString().replaceAll("(animmodels/|\\.json)", "");
		
		return constructor.invoke(armatureName, jointMap.size(), joint, jointMap);
	}
	
	public Joint getJoint(JsonObject object, JsonArray nameAsVertexGroups, Map<String, Joint> jointMap, boolean start) {
		float[] floatArray = ParseUtil.toFloatArray(object.get("transform").getAsJsonArray());
		OpenMatrix4f localMatrix = OpenMatrix4f.load(null, floatArray);
		localMatrix.transpose();
		
		if (start) {
			localMatrix.mulFront(Z_AXIS_TO_Y);
		}
		
		String name = object.get("name").getAsString();
		int index = -1;
		
		for (int i = 0; i < nameAsVertexGroups.size(); i++) {
			if (name.equals(nameAsVertexGroups.get(i).getAsString())) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			throw new IllegalStateException("[ModelParsingError]: Joint name " + name + " not exist!");
		}
		
		Joint joint = new Joint(name, index, localMatrix);
		jointMap.put(name, joint);
		
		for (JsonElement children : object.get("children").getAsJsonArray()) {
			joint.addSubJoint(this.getJoint(children.getAsJsonObject(), nameAsVertexGroups, jointMap, false));
		}
		
		return joint;
	}
	
	public AnimationClip loadClipForAnimation(StaticAnimation animation) {
		if (this.rootJson == null) {
			throw new IllegalStateException("Can't find animation in path: " + animation);
		}
		
		JsonArray array = this.rootJson.get("animation").getAsJsonArray();
		boolean action = animation instanceof ActionAnimation;
		boolean attack = animation instanceof AttackAnimation;
		boolean noTransformData = !action && !attack && FMLEnvironment.dist == Dist.DEDICATED_SERVER;
		boolean root = true;
		Armature armature = animation.getArmature();
		
		Set<String> allowedJoints = Sets.newLinkedHashSet();
		
		if (attack) {
			for (Phase phase : ((AttackAnimation)animation).phases) {
				Joint joint = armature.getRootJoint();
				
				for (AttackAnimation.JointColliderPair colliderInfo : phase.getColliders()) {
					int pathIndex = armature.searchPathIndex(colliderInfo.getFirst().getName());
					
					while (joint != null) {
						allowedJoints.add(joint.getName());
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
		} else if (action) {
			allowedJoints.add("Root");
		}
		
		AnimationClip clip = new AnimationClip();
		
		for (JsonElement element : array) {
			JsonObject keyObject = element.getAsJsonObject();
			String name = keyObject.get("name").getAsString();
			
			if (attack && FMLEnvironment.dist == Dist.DEDICATED_SERVER && !allowedJoints.contains(name)) {
				if (name.equals("Coord")) {
					root = false;
				}
				
				continue;
			}
			
			Joint joint = armature.searchJointByName(name);
			
			if (joint == null) {
				if (name.equals("Coord") && action) {
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
					
					TransformSheet sheet = getTransformSheet(times, transforms, new OpenMatrix4f(), true);
					((ActionAnimation)animation).addProperty(ActionAnimationProperty.COORD, sheet);
					root = false;
					continue;
				} else {
					EpicFightMod.LOGGER.debug("[EpicFightMod] No joint named " + name + " in " + animation);
					continue;
				}
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
			
			if (!noTransformData) {
				clip.addJointTransform(name, sheet);
			}
			
			if (clip.getClipTime() < times[times.length - 1]) {
				clip.setClipTime(times[times.length - 1]);
			}
			
			root = false;
		}
		
		return clip;
	}
	
	public AnimationClip loadAllJointsClipForAnimation(StaticAnimation animation) {
		JsonArray array = this.rootJson.get("animation").getAsJsonArray();
		boolean root = true;
		Armature armature = animation.getArmature();
		AnimationClip clip = new AnimationClip();
		
		for (JsonElement element : array) {
			JsonObject keyObject = element.getAsJsonObject();
			String name = keyObject.get("name").getAsString();
			Joint joint = armature.searchJointByName(name);
			
			if (joint == null) {
				EpicFightMod.LOGGER.warn("[EpicFightMod] Can't find the joint " + name + " in animation data " + animation.getRegistryName());
				continue;
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
			clip.addJointTransform(name, sheet);
			
			if (clip.getClipTime() < times[times.length - 1]) {
				clip.setClipTime(times[times.length - 1]);
			}
			
			root = false;
		}
		
		return clip;
	}
	
	public JsonObject getRootJson() {
		return this.rootJson;
	}
	
	public AnimationClip loadAnimationClip(Armature armature) {
		JsonArray array = this.rootJson.get("animation").getAsJsonArray();
		AnimationClip clip = new AnimationClip();
		boolean root = true;
		
		for (JsonElement element : array) {
			JsonObject keyObject = element.getAsJsonObject();
			String name = keyObject.get("name").getAsString();
			Joint joint = armature.searchJointByName(name);
			
			if (joint == null) {
				continue;
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
			clip.addJointTransform(name, sheet);
			
			if (clip.getClipTime() < times[times.length - 1]) {
				clip.setClipTime(times[times.length - 1]);
			}
			
			root = false;
		}
		
		return clip;
	}
	
	private static TransformSheet getTransformSheet(float[] times, float[] trasnformMatrix, OpenMatrix4f invLocalTransform, boolean correct) {
		List<Keyframe> keyframeList = Lists.newArrayList();
		
		for (int i = 0; i < times.length; i++) {
			float timeStamp = times[i];

			if (timeStamp < 0) {
				continue;
			}
			
			float[] matrixElements = new float[16];

			for (int j = 0; j < 16; j++) {
				matrixElements[j] = trasnformMatrix[i*16 + j];
			}
			
			OpenMatrix4f matrix = OpenMatrix4f.load(null, matrixElements);
			matrix.transpose();
			
			if (correct) {
				matrix.mulFront(Z_AXIS_TO_Y);
			}
			
			matrix.mulFront(invLocalTransform);
			
			JointTransform transform = new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), matrix.toScaleVector());
			keyframeList.add(new Keyframe(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		return sheet;
	}
}