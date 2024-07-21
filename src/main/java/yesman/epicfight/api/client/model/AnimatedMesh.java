package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeRenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.client.model.AnimatedMesh.AnimatedModelPart;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.client.renderer.AnimationShaderInstance;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AnimatedMesh extends Mesh<AnimatedModelPart, BlenderAnimatedVertexBuilder> {
	private final int maxJointCount = -1;
	protected final float[] weights;
	
	public AnimatedMesh(@Nullable Map<String, float[]> arrayMap, @Nullable Map<String, List<BlenderAnimatedVertexBuilder>> partBuilders, @Nullable AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, partBuilders, parent, properties);
		
		this.weights = parent == null ? arrayMap.get("weights") : parent.uvs;
	}
	
	@Override
	protected Map<String, AnimatedModelPart> createModelPart(Map<String, List<BlenderAnimatedVertexBuilder>> partBuilders) {
		Map<String, AnimatedModelPart> parts = Maps.newHashMap();
		
		partBuilders.forEach((partName, vertexBuilder) -> {
			parts.put(partName, new AnimatedModelPart(vertexBuilder));
		});
		
		return parts;
	}
	
	@Override
	protected AnimatedModelPart getOrLogException(Map<String, AnimatedModelPart> parts, String name) {
		if (!parts.containsKey(name)) {
			EpicFightMod.LOGGER.debug("Cannot find the mesh part named " + name + " in " + this.getClass().getCanonicalName());
			return null;
		}
		
		return parts.get(name);
	}
	
	public void draw(PoseStack poseStack, RenderType renderType, BufferBuilder builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay, OpenMatrix4f[] poses) {
		Optional<Supplier<ShaderInstance>> renderTypeShader = ((CompositeRenderType)renderType).state.shaderState.shader;
		
		if (renderTypeShader.isPresent()) {
			ShaderInstance shaderInstance = renderTypeShader.get().get();
			
			if (shaderInstance instanceof AnimationShaderInstance animationShaderInstance) {
				if (animationShaderInstance.POSES != null) {
					for (int i = 0; i < poses.length; i++) {
						animationShaderInstance.POSES[i].set(OpenMatrix4f.exportToMojangMatrix(poses[i]));
					}
				}
			} else {
				throw new RuntimeException(renderType.toString() + " is not using animation shader");
			}
		}
		
		
		for (AnimatedModelPart part : this.parts.values()) {
			part.draw(poseStack, renderType, builder, drawingFunction, packedLight, r, g, b, a, overlay, poses);
		}
		
		renderType.end(builder, RenderSystem.getVertexSorting());
	}
	
	@OnlyIn(Dist.CLIENT)
	public class AnimatedModelPart extends ModelPart<BlenderAnimatedVertexBuilder> {
		public AnimatedModelPart(List<BlenderAnimatedVertexBuilder> animatedMeshPartList) {
			super(animatedMeshPartList);
		}
		
		public void draw(PoseStack poseStack, RenderType renderType, BufferBuilder builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
			if (this.isHidden()) {
				return;
			}
			
			//GlStateManager._vertexAttribPointer(overlayCoord, overlayCoord, overlayCoord, isHidden, packedLight, overlayCoord);
			
			/**
			Matrix4f matrix4f = poseStack.last().pose();
			Matrix3f matrix3f = poseStack.last().normal();
			
			for (BlenderVertexBuilder vi : this.getVertices()) {
				int pos = vi.position * 3;
				int norm = vi.normal * 3;
				int uv = vi.uv * 2;
				Vector4f posVec = new Vector4f(positions[pos], positions[pos + 1], positions[pos + 2], 1.0F);
				Vector3f normVec = new Vector3f(normals[norm], normals[norm + 1], normals[norm + 2]);
				posVec.mul(matrix4f);
				normVec.mul(matrix3f);
				drawingFunction.draw(builder, posVec, normVec, packedLightIn, r, g, b, a, uvs[uv], uvs[uv + 1], overlayCoord);
			}
			**/
		}
	}
	
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
		Int2ObjectMap<BlenderAnimatedVertexBuilder> positionMap = new Int2ObjectOpenHashMap<>();
		int[] vIndices;
		int i = 0;
		
		for (AnimatedModelPart part : this.parts.values()) {
			for (BlenderAnimatedVertexBuilder vertexIndicator : part.getVertices()) {
				indices[i * 3] = vertexIndicator.position;
				indices[i * 3 + 1] = vertexIndicator.uv;
				indices[i * 3 + 2] = vertexIndicator.normal;
				vcounts[vertexIndicator.position] = vertexIndicator.count;
				positionMap.put(vertexIndicator.position, vertexIndicator);
				i++;
			}
		}
		
		for (i = 0; i < vcounts.length; i++) {
			for (int j = 0; j < vcounts[i]; j++) {
				BlenderAnimatedVertexBuilder vi = positionMap.get(i);
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
			
			for (Map.Entry<String, AnimatedModelPart> partEntry : this.parts.entrySet()) {
				IntList indicesArray = new IntArrayList();
				
				for (BlenderAnimatedVertexBuilder vertexIndicator : partEntry.getValue().getVertices()) {
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