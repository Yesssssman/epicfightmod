package yesman.epicfight.api.client.model;

import java.util.Collection;
import java.util.Map;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public abstract class Mesh<T extends VertexIndicator> {
	public static class RenderProperties {
		protected String customTexturePath;
		protected boolean isTransparent;
		
		public String getCustomTexturePath() {
			return this.customTexturePath;
		}
		
		public boolean isTransparent() {
			return this.isTransparent;
		}
		
		public RenderProperties customTexturePath(String path) {
			this.customTexturePath = path;
			return this;
		}
		
		public RenderProperties transparency(boolean isTransparent) {
			this.isTransparent = isTransparent;
			return this;
		}
		
		public static RenderProperties create() {
			return new RenderProperties();
		}
	}
	
	final float[] positions;
	final float[] uvs;
	final float[] normals;
	
	final int totalVertices;
	final Map<String, ModelPart<T>> parts;
	final RenderProperties renderProperties;
	
	public Mesh(Map<String, float[]> arrayMap, Mesh<T> parent, RenderProperties renderProperties, Map<String, ModelPart<T>> parts) {
		this.positions = (parent == null) ? arrayMap.get("positions") : parent.positions;
		this.normals = (parent == null) ? arrayMap.get("normals") : parent.normals;
		this.uvs = (parent == null) ? arrayMap.get("uvs") : parent.uvs;
		this.parts = (parent == null) ? parts : parent.parts;
		this.renderProperties = renderProperties;
		
		int totalV = 0;
		
		for (ModelPart<T> meshpart : parts.values()) {
			totalV += meshpart.getVertices().size();
		}
		
		this.totalVertices = totalV;
	}
	
	protected abstract ModelPart<T> getOrLogException(Map<String, ModelPart<T>> parts, String name);
	
	public boolean hasPart(String part) {
		return this.parts.containsKey(part);
	}
	
	public ModelPart<T> getPart(String part) {
		return this.parts.get(part);
	}
	
	public Collection<ModelPart<T>> getAllParts() {
		return this.parts.values();
	}
	
	public RenderProperties getRenderProperty() {
		return this.renderProperties;
	}
	
	public void initialize() {
		this.parts.values().forEach((part) -> part.hidden = false);
	}
	
	public void drawRawModel(PoseStack poseStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord) {
		Matrix4f matrix4f = poseStack.last().pose();
		Matrix3f matrix3f = poseStack.last().normal();
		
		for (ModelPart<T> part : this.parts.values()) {
			if (!part.hidden) {
				for (VertexIndicator vi : part.getVertices()) {
					int pos = vi.position * 3;
					int norm = vi.normal * 3;
					int uv = vi.uv * 2;
					Vector4f posVec = new Vector4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					Vector3f normVec = new Vector3f(this.normals[norm], this.normals[norm + 1], this.normals[norm + 2]);
					posVec.mul(matrix4f);
					normVec.mul(matrix3f);
					builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, this.uvs[uv], this.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
				}
			}
		}
	}
	
	public void drawRawModelNormal(PoseStack poseStack, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord) {
		Matrix4f matrix4f = poseStack.last().pose();
		
		for (ModelPart<T> part : this.parts.values()) {
			if (!part.hidden) {
				for (VertexIndicator vi : part.getVertices()) {
					int pos = vi.position * 3;
					int uv = vi.uv * 2;
					Vector4f posVec = new Vector4f(this.positions[pos], this.positions[pos + 1], this.positions[pos + 2], 1.0F);
					posVec.mul(matrix4f);
					builder.vertex(posVec.x(), posVec.y(), posVec.z()).color(r, g, b, a).uv(this.uvs[uv], this.uvs[uv + 1]).uv2(packedLightIn).endVertex();
				}
			}
		}
	}
	
	public static class RawMesh extends Mesh<VertexIndicator> {
		public static final ModelPart<VertexIndicator> EMPTY = new ModelPart<>(null, null);
		
		public RawMesh(Map<String, float[]> arrayMap, Mesh<VertexIndicator> parent, RenderProperties properties, Map<String, ModelPart<VertexIndicator>> parts) {
			super(arrayMap, parent, properties, parts);
		}
		
		protected ModelPart<VertexIndicator> getOrLogException(Map<String, ModelPart<VertexIndicator>> parts, String name) {
			if (!parts.containsKey(name)) {
				EpicFightMod.LOGGER.debug("Can not find the mesh part named " + name + " in " + this.getClass().getCanonicalName());
				return EMPTY;
			}
			
			return parts.get(name);
		}
	}
}