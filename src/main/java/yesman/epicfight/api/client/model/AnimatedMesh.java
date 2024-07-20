package yesman.epicfight.api.client.model;

import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AnimatedMesh extends Mesh<AnimatedVertexIndicator> {
	public static final ModelPart<AnimatedVertexIndicator> EMPTY = new ModelPart<> (null, null);
	
	//final float[] weights;
	private final int maxJointCount;
	
	public AnimatedMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		//this.weights = (parent == null) ? arrayMap.get("weights") : parent.weights;
		int maxJointId = 0;
		
		for (Map.Entry<String, ModelPart<AnimatedVertexIndicator>> entry : parts.entrySet()) {
			for (AnimatedVertexIndicator vi : entry.getValue().getVertices()) {
				for (int ji : vi.joint) {
					if (ji > maxJointId) {
						maxJointId = ji;
					}
				}
			}
		}
		
		this.maxJointCount = maxJointId;
	}
	
	@Override
	protected ModelPart<AnimatedVertexIndicator> getOrLogException(Map<String, ModelPart<AnimatedVertexIndicator>> parts, String name) {
		if (!parts.containsKey(name)) {
			EpicFightMod.LOGGER.debug("Cannot find the mesh part named " + name + " in " + this.getClass().getCanonicalName());
			return EMPTY;
		}
		
		return parts.get(name);
	}
	
	public void draw(PoseStack poseStack, MultiBufferSource bufferSource, RenderType renderType, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses) {
		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
		
		for (int i = 0; i < poses.length; i++) {
			if (armature != null) {
				posesNoTranslation[i] = OpenMatrix4f.mul(poses[i], armature.searchJointById(i).getToOrigin(), null).removeTranslation();
			} else {
				posesNoTranslation[i] = poses[i].removeTranslation();
			}
		}
		
		for (ModelPart<AnimatedVertexIndicator> part : this.parts.values()) {
			if (!part.hidden) {
				for (AnimatedVertexIndicator vi : part.getVertices()) {
					int pos = vi.position * 3;
					int norm = vi.normal * 3;
					int uv = vi.uv * 2;
					
					Vec4f position = new Vec4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					Vec4f normal = new Vec4f(this.normals[norm], this.normals[norm + 1], this.normals[norm + 2], 1.0F);
					/**
					Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					
					for (int i = 0; i < vi.joint.size(); i++) {
						int jointIndex = vi.joint.getInt(i);
						int weightIndex = vi.weight.getInt(i);
						float weight = this.weights[weightIndex];
						
						if (armature != null) {
							Vec4f.add(OpenMatrix4f.transform(OpenMatrix4f.mul(poses[jointIndex], armature.searchJointById(jointIndex).getToOrigin(), null), position, null).scale(weight), totalPos, totalPos);
						} else {
							Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
						}

						Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
					}**/
					
					Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
					Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
					posVec.mul(matrix4f);
					normVec.mul(matrix3f);
					
					drawingFunction.draw(builder, posVec, normVec, packedLight, r, g, b, a, this.uvs[uv], this.uvs[uv + 1], overlay);
				}
			}
		}
	}
	
	/**
	public void drawModelWithPose(PoseStack poseStack, VertexConsumer builder, int packedLight, float r, float g, float b, float a, int overlayCoord, Armature armature, OpenMatrix4f[] poses) {
		this.draw(poseStack, builder, packedLight, r, g, b, a, overlayCoord, armature, poses);
	}
	
	public void drawWithPoseNoTexture(PoseStack poseStack, VertexConsumer builder, int packedLight, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
		this.draw(poseStack, builder, packedLight, r, g, b, a, overlayCoord, null, poses);
	}
	**/
	
	public JsonObject toJsonObject() {
		JsonObject root = new JsonObject();
		JsonObject vertices = new JsonObject();
		float[] positions = this.positions.clone();
		float[] normals = this.normals.clone();
		OpenMatrix4f correctRevert = OpenMatrix4f.invert(JsonModelLoader.BLENDER_TO_MINECRAFT_COORD, null);
		
		for (int i = 0; i < positions.length / 3; i++) {
			int k = i * 3;
			Vec4f posVector = new Vec4f(positions[k], positions[k+1], positions[k+2], 1.0F);
			posVector.transform(correctRevert);
			positions[k] = posVector.x;
			positions[k+1] = posVector.y;
			positions[k+2] = posVector.z;
		}
		
		for (int i = 0; i < normals.length / 3; i++) {
			int k = i * 3;
			Vec4f normVector = new Vec4f(normals[k], normals[k+1], normals[k+2], 1.0F);
			normVector.transform(correctRevert);
			normals[k] = normVector.x;
			normals[k+1] = normVector.y;
			normals[k+2] = normVector.z;
		}
		
		int[] indices = new int[this.totalVertices * 3];
		int[] vcounts = new int[positions.length / 3];
		IntList vIndexList = new IntArrayList();
		Int2ObjectMap<AnimatedVertexIndicator> positionMap = new Int2ObjectOpenHashMap<>();
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
				vIndexList.add(vi.joint.getInt(j));
				vIndexList.add(vi.weight.getInt(j));
			}
		}
		
		vIndices = vIndexList.toIntArray();
		vertices.add("positions", ParseUtil.arrayToJsonObject(positions, 3));
		vertices.add("uvs", ParseUtil.arrayToJsonObject(this.uvs, 2));
		vertices.add("normals", ParseUtil.arrayToJsonObject(normals, 3));
		
		if (!this.parts.isEmpty()) {
			JsonObject parts = new JsonObject();
			
			for (Map.Entry<String, ModelPart<VertexIndicator.AnimatedVertexIndicator>> partEntry : this.parts.entrySet()) {
				IntList indicesArray = new IntArrayList();
				
				for (VertexIndicator.AnimatedVertexIndicator vertexIndicator : partEntry.getValue().getVertices()) {
					indicesArray.add(vertexIndicator.position);
					indicesArray.add(vertexIndicator.uv);
					indicesArray.add(vertexIndicator.normal);
				}
				
				parts.add(partEntry.getKey(), ParseUtil.arrayToJsonObject(indicesArray.toIntArray(), 3));
			}
			
			vertices.add("parts", parts);
		} else {
			vertices.add("indices", ParseUtil.arrayToJsonObject(indices, 3));
		}
		
		vertices.add("vcounts", ParseUtil.arrayToJsonObject(vcounts, 1));
		vertices.add("weights", ParseUtil.arrayToJsonObject(this.weights, 1));
		vertices.add("vindices", ParseUtil.arrayToJsonObject(vIndices, 1));
		root.add("vertices", vertices);
		
		if (this.renderProperties != null) {
			JsonObject renderProperties = new JsonObject();
			renderProperties.addProperty("texture_path", this.renderProperties.getCustomTexturePath());
			renderProperties.addProperty("transparent", this.renderProperties.isTransparent());
			root.add("render_properties", renderProperties);
		}
		
		return root;
	}
	
	public int getMaxJointCount() {
		return this.maxJointCount;
	}
}