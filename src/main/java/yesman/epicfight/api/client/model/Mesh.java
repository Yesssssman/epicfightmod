package yesman.epicfight.api.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Mesh<P extends ModelPart<V>, V extends VertexBuilder> {
	protected final float[] positions;
	protected final float[] normals;
	protected final float[] uvs;
	
	protected final int vertexCount;
	protected final RenderProperties renderProperties;
	protected final Map<String, P> parts;
	
	/**
	 * @param arrayMap Null if parent is not null
	 * @param partBuilders Null if parent is not null
	 * @param parent Null if arrayMap and parts are not null
	 * @param renderProperties
	 */
	public Mesh(@Nullable Map<String, float[]> arrayMap, @Nullable Map<String, List<V>> partBuilders, @Nullable Mesh<P, V> parent, RenderProperties renderProperties) {
		this.positions = (parent == null) ? arrayMap.get("positions") : parent.positions;
		this.normals = (parent == null) ? arrayMap.get("normals") : parent.normals;
		this.uvs = (parent == null) ? arrayMap.get("uvs") : parent.uvs;
		this.renderProperties = renderProperties;
		this.parts = (parent == null) ? this.createModelPart(partBuilders) : parent.parts;
		
		int totalV = 0;
		
		for (ModelPart<V> modelpart : this.parts.values()) {
			totalV += modelpart.getVertices().size();
		}
		
		this.vertexCount = totalV;
	}
	
	protected abstract Map<String, P> createModelPart(Map<String, List<V>> partBuilders);
	protected abstract P getOrLogException(Map<String, P> parts, String name);
	public abstract void draw(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay);
	
	public boolean hasPart(String part) {
		return this.parts.containsKey(part);
	}
	
	public ModelPart<V> getPart(String part) {
		return this.parts.get(part);
	}
	
	public Collection<P> getAllParts() {
		return this.parts.values();
	}
	
	public RenderProperties getRenderProperty() {
		return this.renderProperties;
	}
	
	public void initialize() {
		this.parts.values().forEach((part) -> part.setHidden(false));
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class RenderProperties {
		protected String customTexturePath;
		protected boolean isTransparent;
		protected Object2BooleanMap<String> parentPartVisualizer;
		
		public String getCustomTexturePath() {
			return this.customTexturePath;
		}
		
		public boolean isTransparent() {
			return this.isTransparent;
		}
		
		public Object2BooleanMap<String> getParentPartVisualizer() {
			return this.parentPartVisualizer;
		}
		
		public RenderProperties customTexturePath(String path) {
			this.customTexturePath = path;
			return this;
		}
		
		public RenderProperties transparency(boolean isTransparent) {
			this.isTransparent = isTransparent;
			return this;
		}
		
		public RenderProperties newPartVisualizer(String partName, boolean setVisible) {
			if (this.parentPartVisualizer == null) {
				this.parentPartVisualizer = new Object2BooleanOpenHashMap<>();
			}
			
			this.parentPartVisualizer.put(partName, setVisible);
			
			return this;
		}
		
		public static RenderProperties create() {
			return new RenderProperties();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@FunctionalInterface
	public interface DrawingFunction {
		public static final DrawingFunction ENTITY_TEXTURED = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.vertex(posX, posY, posZ, r, g, b, a, u, v, overlay, packedLight, normX, normY, normZ);
		};
		
		public static final DrawingFunction ENTITY_PARTICLE = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.vertex(posX, posY, posZ);
			builder.color(r, g, b, a);
			builder.uv2(packedLight);
			builder.endVertex();
		};
		
		public static final DrawingFunction ENTITY_SOLID = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.vertex(posX, posY, posZ);
			builder.color(r, g, b, a);
			builder.normal(normX, normY, normZ);
			builder.endVertex();
		};
		
		public static final DrawingFunction ENTITY_NO_LIGHTING = (builder, posX, posY, posZ, normX, normY, normZ, packedLight, r, g, b, a, u, v, overlay) -> {
			builder.vertex(posX, posY, posZ);
			builder.color(r, g, b, a);
			builder.uv(u, v);
			builder.uv2(packedLight);
			builder.endVertex();
		};
		
		public void draw(VertexConsumer builder, float posX, float posY, float posZ, float normX, float normY, float normZ, int packedLight, float r, float g, float b, float a, float u, float v, int overlay);
	}
}