package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AnimatedMesh extends Mesh<AnimatedVertexIndicator> {
	public static final ModelPart<AnimatedVertexIndicator> EMPTY = new ModelPart<>(null);
	final float[] weights;
	
	public AnimatedMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.weights = (parent == null) ? arrayMap.get("weights") : parent.weights;
	}
	
	@Override
	protected ModelPart<AnimatedVertexIndicator> getOrLogException(Map<String, ModelPart<AnimatedVertexIndicator>> parts, String name) {
		if (!parts.containsKey(name)) {
			if (EpicFightMod.LOGGER.isDebugEnabled()) {
				EpicFightMod.LOGGER.debug("Cannot find the mesh part named " + name + " in " + this.getClass().getCanonicalName());
			}
			
			return EMPTY;
		}
		
		return parts.get(name);
	}
	
	public void drawModelWithPose(PoseStack poseStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, Armature armature, OpenMatrix4f[] poses) {
		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
		
		for (int i = 0; i < poses.length; i++) {
			posesNoTranslation[i] = OpenMatrix4f.mul(poses[i], armature.searchJointById(i).getToOrigin(), null).removeTranslation();
		}
		
		for (ModelPart<AnimatedVertexIndicator> part : this.parts.values()) {
			if (!part.hidden) {
				for (AnimatedVertexIndicator vi : part.getVertices()) {
					int pos = vi.position * 3;
					int norm = vi.normal * 3;
					int uv = vi.uv * 2;
					Vec4f position = new Vec4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					Vec4f normal = new Vec4f(this.normals[norm], this.normals[norm + 1], this.normals[norm + 2], 1.0F);
					Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					
					for (int i = 0; i < vi.joint.size(); i++) {
						int jointIndex = vi.joint.get(i);
						int weightIndex = vi.weight.get(i);
						float weight = this.weights[weightIndex];
						Vec4f.add(OpenMatrix4f.transform(OpenMatrix4f.mul(poses[jointIndex], armature.searchJointById(jointIndex).getToOrigin(), null), position, null).scale(weight), totalPos, totalPos);
						Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
					}
					
					Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
					Vector3f normVec = totalNorm.toMojangVector();
					posVec.transform(matrix4f);
					normVec.transform(matrix3f);
					builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, this.uvs[uv], this.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
				}
			}
		}
	}
	
	public void drawWithPoseNoTexture(PoseStack poseStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
		
		for (int i = 0; i < poses.length; i++) {
			posesNoTranslation[i] = poses[i].removeTranslation();
		}
		
		for (ModelPart<AnimatedVertexIndicator> part : this.parts.values()) {
			if (!part.hidden) {
				for (AnimatedVertexIndicator vi : part.getVertices()) {
					int pos = vi.position * 3;
					int norm = vi.normal * 3;
					Vec4f position = new Vec4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					Vec4f normal = new Vec4f(this.normals[norm], this.normals[norm + 1], this.normals[norm + 2], 1.0F);
					Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					
					for (int i = 0; i < vi.joint.size(); i++) {
						int jointIndex = vi.joint.get(i);
						int weightIndex = vi.weight.get(i);
						float weight = this.weights[weightIndex];
						Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
						Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
					}
					
					Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
					Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
					posVec.transform(matrix4f);
					normVec.transform(matrix3f);
					builder.vertex(posVec.x(), posVec.y(), posVec.z());
					builder.color(r, g, b, a);
					builder.uv2(packedLightIn);
					builder.endVertex();
				}
			}
		}
	}
	
	public JsonObject toJsonObject() {
		JsonObject root = new JsonObject();
		JsonObject vertices = new JsonObject();
		float[] positions = this.positions.clone();
		float[] normals = this.normals.clone();
		
		OpenMatrix4f toBlenderCoord = OpenMatrix4f.createRotatorDeg(90.0F, Vec3f.X_AXIS);
		
		for (int i = 0; i < positions.length / 3; i++) {
			int k = i * 3;
			Vec4f posVector = new Vec4f(positions[k], positions[k+1], positions[k+2], 1.0F);
			OpenMatrix4f.transform(toBlenderCoord, posVector, posVector);
			positions[k] = posVector.x;
			positions[k+1] = posVector.y;
			positions[k+2] = posVector.z;
		}
		
		for (int i = 0; i < normals.length / 3; i++) {
			int k = i * 3;
			Vec4f normVector = new Vec4f(normals[k], normals[k+1], normals[k+2], 1.0F);
			OpenMatrix4f.transform(toBlenderCoord, normVector, normVector);
			normals[k] = normVector.x;
			normals[k+1] = normVector.y;
			normals[k+2] = normVector.z;
		}
		
		int[] indices = new int[this.totalVertices * 3];
		int[] vcounts = new int[positions.length / 3];
		List<Integer> vIndexList = Lists.newArrayList();
		Map<Integer, AnimatedVertexIndicator> positionMap = Maps.newHashMap();
		int[] vIndices;
		int i = 0;
		
		for (ModelPart<AnimatedVertexIndicator> part : this.parts.values()) {
			for (AnimatedVertexIndicator vertexIndicator : part.getVertices()) {
				indices[i * 3] = vertexIndicator.position;
				indices[i * 3 + 1] = vertexIndicator.uv;
				indices[i * 3 + 2] = vertexIndicator.normal;
				vcounts[vertexIndicator.position] = vertexIndicator.joint.size();
				positionMap.put(vertexIndicator.position, vertexIndicator);
				i++;
			}
		}
		
		for (i = 0; i < vcounts.length; i++) {
			for (int j = 0; j < vcounts[i]; j++) {
				AnimatedVertexIndicator vi = positionMap.get(i);
				vIndexList.add(vi.joint.get(j));
				vIndexList.add(vi.weight.get(j));
			}
		}
		
		vIndices = vIndexList.stream().mapToInt(j -> j).toArray();
		vertices.add("positions", arrayToJsonObject(positions, 3));
		vertices.add("uvs", arrayToJsonObject(this.uvs, 2));
		vertices.add("normals", arrayToJsonObject(normals, 3));
		vertices.add("indices", arrayToJsonObject(indices, 3));
		vertices.add("vcounts", arrayToJsonObject(vcounts, 1));
		vertices.add("weights", arrayToJsonObject(this.weights, 1));
		vertices.add("vindices", arrayToJsonObject(vIndices, 1));
		root.add("vertices", vertices);
		
		return root;
	}
	
	public static JsonObject arrayToJsonObject(float[] array, int stride) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("stride", stride);
		jsonObject.addProperty("count", array.length / stride);
		JsonArray jsonArray = new JsonArray();
		
		for (float element : array) {
			jsonArray.add(element);
		}
		
		jsonObject.add("array", jsonArray);
		
		return jsonObject;
	}
	
	public static JsonObject arrayToJsonObject(int[] array, int stride) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("stride", stride);
		jsonObject.addProperty("count", array.length / stride);
		JsonArray jsonArray = new JsonArray();
		
		for (int element : array) {
			jsonArray.add(element);
		}
		
		jsonObject.add("array", jsonArray);
		
		return jsonObject;
	}
}