package yesman.epicfight.api.client.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh.AnimatedModelPart;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.EpicFightVertexFormatElement;
import yesman.epicfight.client.renderer.shader.AnimationShaderInstance;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class AnimatedMesh extends Mesh<AnimatedModelPart, AnimatedVertexBuilder> {
	protected final float[] weights;
	private final int maxJointCount;
	private int arrayObjectId;
	
	private VertexBuffer<Float> positionsBuffer = new VertexBuffer<> (GL11.GL_FLOAT, 3, false, ByteBuffer::putFloat);
	private VertexBuffer<Float> uvsBuffer = new VertexBuffer<> (GL11.GL_FLOAT, 2, false, ByteBuffer::putFloat);
	private VertexBuffer<Byte> normalsBuffer = new VertexBuffer<> (GL11.GL_BYTE, 3, true, ByteBuffer::put);
	private VertexBuffer<Short> jointsBuffer = new VertexBuffer<> (GL11.GL_SHORT, 3, false, ByteBuffer::putShort);
	private VertexBuffer<Float> weightsBuffer = new VertexBuffer<> (GL11.GL_FLOAT, 3, false, ByteBuffer::putFloat);
	
	public AnimatedMesh(@Nullable Map<String, float[]> arrayMap, @Nullable Map<String, List<AnimatedVertexBuilder>> partBuilders, @Nullable AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, partBuilders, parent, properties);
		
		this.weights = parent == null ? arrayMap.get("weights") : parent.weights;
		int maxJointId = 0;
		
		for (Map.Entry<String, AnimatedModelPart> entry : this.parts.entrySet()) {
			for (AnimatedVertexBuilder vi : entry.getValue().getVertices()) {
				if (maxJointId < vi.joint.x) {
					maxJointId = vi.joint.x;
				}
				
				if (maxJointId < vi.joint.y) {
					maxJointId = vi.joint.y;
				}
				
				if (maxJointId < vi.joint.z) {
					maxJointId = vi.joint.z;
				}
			}
		}
		
		this.maxJointCount = maxJointId;
		this.arrayObjectId = GL30.glGenVertexArrays();
		
		List<Float> positionList = Lists.newArrayList();
		List<Float> uvList = Lists.newArrayList();
		List<Byte> normalList = Lists.newArrayList();
		List<Short> jointList = Lists.newArrayList();
		List<Float> weightList = Lists.newArrayList();
		Map<AnimatedVertexBuilder, Integer> vertexBuilderMap = Maps.newHashMap();
		
		int currentBoundVao = GlStateManager._getInteger(GL30.GL_VERTEX_ARRAY_BINDING);
		int currentBoundVbo = GlStateManager._getInteger(GL30.GL_VERTEX_ARRAY_BUFFER_BINDING);
		
		GlStateManager._glBindVertexArray(this.arrayObjectId);
		
		for (AnimatedModelPart part : this.parts.values()) {
			part.createVbo(vertexBuilderMap, this.positions, this.uvs, this.normals, this.weights, positionList, uvList, normalList, jointList, weightList);
		}
		
		this.positionsBuffer.bindVertexData(positionList);
		this.uvsBuffer.bindVertexData(uvList);
		this.normalsBuffer.bindVertexData(normalList);
		this.jointsBuffer.bindVertexData(jointList);
		this.weightsBuffer.bindVertexData(weightList);
		
		GlStateManager._glBindVertexArray(currentBoundVao);
		GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBoundVbo);
	}
	
	public void pointPositionsBuffer(int attrIndex) {
		this.positionsBuffer.vertexAttribPointer(attrIndex);
	}
	
	public void uvPositionsBuffer(int attrIndex) {
		this.uvsBuffer.vertexAttribPointer(attrIndex);
	}
	
	public void normalPositionsBuffer(int attrIndex) {
		this.normalsBuffer.vertexAttribPointer(attrIndex);
	}
	
	public void jointPositionsBuffer(int attrIndex) {
		this.jointsBuffer.vertexAttribPointer(attrIndex);
	}
	
	public void weightPositionsBuffer(int attrIndex) {
		this.weightsBuffer.vertexAttribPointer(attrIndex);
	}
	
	public void destroy() {
		this.positionsBuffer.destroy();
		this.uvsBuffer.destroy();
		this.normalsBuffer.destroy();
		this.jointsBuffer.destroy();
		this.weightsBuffer.destroy();
        this.parts.values().forEach(part -> RenderSystem.glDeleteBuffers(part.indexBufferId));
        
        RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
        this.arrayObjectId = -1;
	}
	
	@Override
	protected Map<String, AnimatedModelPart> createModelPart(Map<String, List<AnimatedVertexBuilder>> partBuilders) {
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
	
	/**
	 * Draws the model without applying animation
	 */
	@Override
	public void draw(PoseStack poseStack, VertexConsumer vertexConsumer, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay) {
		for (AnimatedModelPart part : this.parts.values()) {
			part.draw(poseStack, vertexConsumer, drawingFunction, packedLight, r, g, b, a, overlay);
		}
	}
	
	/**
	 * Draws the model depending on animation shader option
	 * @param armature give this parameter as null if @param poses already bound origin translation
	 * @param poses
	 */
	public void draw(PoseStack poseStack, MultiBufferSource multiBufferSource, RenderType renderType, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses) {
		if (EpicFightMod.CLIENT_CONFIGS.useAnimationShader.getValue()) {
			renderType.setupRenderState();
			AnimationShaderInstance animationShader = EpicFightRenderTypes.getAnimationShader(renderType);
			this.drawWithShader(poseStack, animationShader, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
			renderType.clearRenderState();
		} else {
			VertexConsumer vertexConsumer = multiBufferSource.getBuffer(EpicFightRenderTypes.getTriangulated(renderType));
			this.drawToBuffer(poseStack, vertexConsumer, Mesh.DrawingFunction.ENTITY_TEXTURED, packedLight, r, g, b, a, overlay, armature, poses);
		}
	}
	
	/**
	 * Draws the model to vanilla buffer
	 */
	public void drawToBuffer(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses) {
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
		
		for (ModelPart<AnimatedVertexBuilder> part : this.parts.values()) {
			if (!part.isHidden()) {
				for (AnimatedVertexBuilder vi : part.getVertices()) {
					int pos = vi.position * 3;
					int norm = vi.normal * 3;
					int uv = vi.uv * 2;
					
					Vec4f position = new Vec4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					Vec4f normal = new Vec4f(this.normals[norm], this.normals[norm + 1], this.normals[norm + 2], 1.0F);
					Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
					
					for (int i = 0; i < vi.count; i++) {
						int jointIndex = vi.getJointId(i);
						int weightIndex = vi.getWeightIndex(i);
						float weight = this.weights[weightIndex];
						
						if (armature != null) {
							Vec4f.add(OpenMatrix4f.transform(OpenMatrix4f.mul(poses[jointIndex], armature.searchJointById(jointIndex).getToOrigin(), null), position, null).scale(weight), totalPos, totalPos);
						} else {
							Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
						}

						Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
					}
					
					Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
					Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
					posVec.mul(matrix4f);
					normVec.mul(matrix3f);
					
					drawingFunction.draw(builder, posVec.x, posVec.y, posVec.z, normVec.x, normVec.y, normVec.z, packedLight, r, g, b, a, this.uvs[uv], this.uvs[uv + 1], overlay);
				}
			}
		}
	}
	
	/**
	 * Draw the model with shader optimization by shader and vertex format
	 */
	public void drawWithShader(PoseStack poseStack, ShaderInstance shader, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses) {
		AnimationShaderInstance animationShader = EpicFightRenderTypes.getAnimationShader(shader);
		this.drawWithShader(poseStack, animationShader, packedLight, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
	}
	
	public void drawWithShader(PoseStack poseStack, AnimationShaderInstance animationShaderInstance, int packedLight, float r, float g, float b, float a, int overlay, Armature armature, OpenMatrix4f[] poses) {
		if (this.arrayObjectId < 0) {
			throw new IllegalStateException("Mesh destroyed");
		}
		
		for (int i = 0; i < 12; ++i) {
			int j = RenderSystem.getShaderTexture(i);
			animationShaderInstance.setSampler("Sampler" + i, j);
		}
		
		if (animationShaderInstance.getModelViewMatrixUniform() != null) {
			animationShaderInstance.getModelViewMatrixUniform().set(poseStack.last().pose());
		}
		
		if (animationShaderInstance.getProjectionMatrixUniform() != null) {
			animationShaderInstance.getProjectionMatrixUniform().set(RenderSystem.getProjectionMatrix());
		}
		
		if (animationShaderInstance.getNormalMatrixUniform() != null) {
			animationShaderInstance.getNormalMatrixUniform().set(poseStack.last().normal());
		}
		
		if (animationShaderInstance.getInverseViewRotationMatrixUniform() != null) {
			animationShaderInstance.getInverseViewRotationMatrixUniform().set(RenderSystem.getInverseViewRotationMatrix());
		}
		
		if (animationShaderInstance.getColorModulatorUniform() != null) {
			animationShaderInstance.getColorModulatorUniform().set(RenderSystem.getShaderColor());
		}
		
		if (animationShaderInstance.getGlintAlphaUniform() != null) {
			animationShaderInstance.getGlintAlphaUniform().set(RenderSystem.getShaderGlintAlpha());
		}
		
		if (animationShaderInstance.getFogStartUniform() != null) {
			animationShaderInstance.getFogStartUniform().set(RenderSystem.getShaderFogStart());
		}
		
		if (animationShaderInstance.getFogEndUniform() != null) {
			animationShaderInstance.getFogEndUniform().set(RenderSystem.getShaderFogEnd());
		}
		
		if (animationShaderInstance.getFogColorUniform() != null) {
			animationShaderInstance.getFogColorUniform().set(RenderSystem.getShaderFogColor());
		}
		
		if (animationShaderInstance.getFogShapeUniform() != null) {
			animationShaderInstance.getFogShapeUniform().set(RenderSystem.getShaderFogShape().getIndex());
		}
		
		if (animationShaderInstance.getTextureMatrixUniform() != null) {
			animationShaderInstance.getTextureMatrixUniform().set(RenderSystem.getTextureMatrix());
		}
		
		if (animationShaderInstance.getGameTimeUniform() != null) {
			animationShaderInstance.getGameTimeUniform().set(RenderSystem.getShaderGameTime());
		}
		
		if (animationShaderInstance.getScreenSizeUniform() != null) {
			Window window = Minecraft.getInstance().getWindow();
			animationShaderInstance.getScreenSizeUniform().set((float) window.getWidth(), (float) window.getHeight());
		}
		
		if (animationShaderInstance.getColorUniform() != null) {
			animationShaderInstance.getColorUniform().set(r, g, b, a);
		}
		
		if (animationShaderInstance.getOverlayUniform() != null) {
			animationShaderInstance.getOverlayUniform().set(overlay & '\uffff', overlay >> 16 & '\uffff');
		}
		
		if (animationShaderInstance.getLightUniform() != null) {
			animationShaderInstance.getLightUniform().set(packedLight & '\uffff', packedLight >> 16 & '\uffff');
		}
		
		for (int i = 0; i < poses.length; i++) {
			if (animationShaderInstance.getPoses(i) != null) {
				animationShaderInstance.getPoses(i).set(OpenMatrix4f.exportToMojangMatrix(armature == null ? poses[i] : OpenMatrix4f.mul(poses[i], armature.searchJointById(i).getToOrigin(), null)));
			}
		}
		
		animationShaderInstance.setupShaderLights();
		
		int currentBoundVao = GlStateManager._getInteger(GL30.GL_VERTEX_ARRAY_BINDING);
		int currentBoundVbo = GlStateManager._getInteger(GL30.GL_VERTEX_ARRAY_BUFFER_BINDING);
		
		GlStateManager._glBindVertexArray(this.arrayObjectId);
		EpicFightVertexFormatElement.bindDrawing(this);
		
		animationShaderInstance._getVertexFormat().setupBufferState();
		animationShaderInstance._apply();
		
		for (AnimatedModelPart part : this.parts.values()) {
			part.drawWithShader();
		}
		
		animationShaderInstance._clear();
		animationShaderInstance._getVertexFormat().clearBufferState();
		
		EpicFightVertexFormatElement.unbindDrawing();
		
		GlStateManager._glBindVertexArray(currentBoundVao);
		GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, currentBoundVbo);
	}
	
	public int getMaxJointCount() {
		return this.maxJointCount;
	}
	
	@OnlyIn(Dist.CLIENT)
	public class AnimatedModelPart extends ModelPart<AnimatedVertexBuilder> {
		private int indexBufferId;
		
		public AnimatedModelPart(List<AnimatedVertexBuilder> animatedMeshPartList) {
			super(animatedMeshPartList);
		}
		
		private void createVbo(Map<AnimatedVertexBuilder, Integer> vertexBuilderMap, float positions[], float uvs[], float normals[], float weights[], List<Float> position, List<Float> uv, List<Byte> normal, List<Short> joint, List<Float> weight) {
			ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(this.getVertices().size() * 4).order(ByteOrder.nativeOrder());
			
			for (AnimatedVertexBuilder vb : this.getVertices()) {
				if (vertexBuilderMap.containsKey(vb)) {
					indicesBuffer.putInt(vertexBuilderMap.get(vb));
				} else {
					int next = vertexBuilderMap.size();
					indicesBuffer.putInt(next);
					vertexBuilderMap.put(vb, next);
					position.add(positions[vb.position * 3]);
					position.add(positions[vb.position * 3 + 1]);
					position.add(positions[vb.position * 3 + 2]);
					uv.add(uvs[vb.uv * 2]);
					uv.add(uvs[vb.uv * 2 + 1]);
					normal.add(normalIntValue(normals[vb.normal * 3]));
					normal.add(normalIntValue(normals[vb.normal * 3 + 1]));
					normal.add(normalIntValue(normals[vb.normal * 3 + 2]));
					joint.add((short)vb.joint.x);
					joint.add((short)vb.joint.y);
					joint.add((short)vb.joint.z);
					weight.add(vb.weight.x > -1 ? weights[vb.weight.x] : 0.0F);
					weight.add(vb.weight.y > -1 ? weights[vb.weight.y] : 0.0F);
					weight.add(vb.weight.z > -1 ? weights[vb.weight.z] : 0.0F);
				}
			}
			
			indicesBuffer.flip();
			
			this.indexBufferId = GL15.glGenBuffers();
			GlStateManager._glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
			GlStateManager._glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
			GlStateManager._glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		
		@Override
		public void draw(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay) {
			if (this.isHidden()) {
				return;
			}
			
			Matrix4f matrix4f = poseStack.last().pose();
			Matrix3f matrix3f = poseStack.last().normal();
			
			for (AnimatedVertexBuilder vi : this.getVertices()) {
				int pos = vi.position * 3;
				int norm = vi.normal * 3;
				int uv = vi.uv * 2;
				Vector4f posVec = matrix4f.transform(new Vector4f(positions[pos], positions[pos + 1], positions[pos + 2], 1.0F));
				Vector3f normVec = matrix3f.transform(new Vector3f(normals[norm], normals[norm + 1], normals[norm + 2]));
				
				drawingFunction.draw(builder, posVec.x(), posVec.x(), posVec.z(), normVec.x(), normVec.y(), normVec.z(), packedLight, r, g, b, a, uvs[uv], uvs[uv + 1], overlay);
			}
		}
		
		public void drawWithShader() {
			if (this.isHidden()) {
				return;
			}
			
			GlStateManager._glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
			RenderSystem.drawElements(VertexFormat.Mode.TRIANGLES.asGLMode, this.getVertices().size(), VertexFormat.IndexType.INT.asGLType);
			GlStateManager._glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
		
		static byte normalIntValue(float f) {
			return (byte)((int)(Mth.clamp(f, -1.0F, 1.0F) * 127.0F) & 255);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class VertexBuffer<T extends Number> {
		private int vertexBufferIds;
		private final int glType;
		private final int size;
		private final boolean normalize;
		private final BiConsumer<ByteBuffer, T> bufferUploader;
		
		public VertexBuffer(int glType, int size, boolean normalize, BiConsumer<ByteBuffer, T> bufferUploader) {
			this.vertexBufferIds = GL15.glGenBuffers();
			this.glType = glType;
			this.size = size;
			this.normalize = normalize;
			this.bufferUploader = bufferUploader;
		}
		
		public void bindVertexData(List<T> data) {
			if (this.vertexBufferIds < 0) {
				throw new RuntimeException("vertex buffer is already destroyed");
			}
			
			ByteBuffer buf = ByteBuffer.allocateDirect(data.size() * 4).order(ByteOrder.nativeOrder());
			
			for (T f : data) {
				this.bufferUploader.accept(buf, f);
			}
			
			buf.flip();
			
			GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferIds);
			GlStateManager._glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);
			GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		
		public void vertexAttribPointer(int attrIndex) {
			if (this.vertexBufferIds < 0) {
				throw new RuntimeException("vertex buffer is already destroyed");
			}
			
			GlStateManager._glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vertexBufferIds);
			
			switch (this.glType) {
			case GL11.GL_DOUBLE, GL11.GL_FLOAT -> {
				GlStateManager._vertexAttribPointer(attrIndex, this.size, this.glType, this.normalize, 0, 0);
			}
			case GL11.GL_BYTE, GL11.GL_SHORT, GL11.GL_INT -> {
				if (this.normalize) {
					GlStateManager._vertexAttribPointer(attrIndex, this.size, this.glType, true, 0, 0);
				} else {
					GlStateManager._vertexAttribIPointer(attrIndex, this.size, this.glType, 0, 0);
				}
			}
			}
		}
		
		public void destroy() {
			RenderSystem.glDeleteBuffers(this.vertexBufferIds);
			this.vertexBufferIds = -1;
		}
	}
	
	/**
	 * Export this model as Json format
	 */
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
		
		int[] indices = new int[this.vertexCount * 3];
		int[] vcounts = new int[positions.length / 3];
		IntList vIndexList = new IntArrayList();
		Int2ObjectMap<AnimatedVertexBuilder> positionMap = new Int2ObjectOpenHashMap<>();
		int[] vIndices;
		int i = 0;
		
		for (AnimatedModelPart part : this.parts.values()) {
			for (AnimatedVertexBuilder vertexIndicator : part.getVertices()) {
				indices[i * 3] = vertexIndicator.position;
				indices[i * 3 + 1] = vertexIndicator.uv;
				indices[i * 3 + 2] = vertexIndicator.normal;
				vcounts[vertexIndicator.position] = vertexIndicator.count;
				positionMap.put(vertexIndicator.position, vertexIndicator);
				i++;
			}
		}
		
		for (i = 0; i < vcounts.length; i++) {
			AnimatedVertexBuilder vi = positionMap.get(i);
			
			switch (vcounts[i]) {
			case 1 -> {
				vIndexList.add(vi.joint.x);
				vIndexList.add(vi.weight.x);
			}
			case 2 -> {
				vIndexList.add(vi.joint.x);
				vIndexList.add(vi.weight.x);
				vIndexList.add(vi.joint.y);
				vIndexList.add(vi.weight.y);
			}
			case 3 -> {
				vIndexList.add(vi.joint.x);
				vIndexList.add(vi.weight.x);
				vIndexList.add(vi.joint.y);
				vIndexList.add(vi.weight.y);
				vIndexList.add(vi.joint.z);
				vIndexList.add(vi.weight.z);
			}
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
				
				for (AnimatedVertexBuilder vertexIndicator : partEntry.getValue().getVertices()) {
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
}