package yesman.epicfight.api.client.model;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModelPart<T extends VertexBuilder> {
	protected final List<T> verticies;
	protected final net.minecraft.client.model.geom.ModelPart vanillaModelPart;
	protected boolean isHidden;
	
	public ModelPart(List<T> vertices) {
		this(vertices, null);
	}
	
	public ModelPart(List<T> vertices, @Nullable net.minecraft.client.model.geom.ModelPart vanillaModelPart) {
		this.verticies = vertices;
		this.vanillaModelPart = vanillaModelPart;
	}
	
	public abstract void draw(PoseStack poseStack, VertexConsumer builder, Mesh.DrawingFunction drawingFunction, int packedLight, float r, float g, float b, float a, int overlay);
	
	public void setHidden(boolean hidden) {
		this.isHidden = hidden;
	}
	
	public boolean isHidden() {
		return this.isHidden;
	}
	
	public List<T> getVertices() {
		return this.verticies;
	}
	
	/**
	public void setVanillaTransfrom() {
		if (this.vanillaModelPart != null) {
			
		}
	}
	**/
}