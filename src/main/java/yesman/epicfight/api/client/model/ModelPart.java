package yesman.epicfight.api.client.model;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPart<T extends VertexIndicator> {
	private final net.minecraft.client.model.geom.ModelPart vanillaModelPart;
	private final List<T> vertices;
	public boolean hidden;
	
	public ModelPart(List<T> vertices) {
		this(vertices, null);
	}
	
	public ModelPart(List<T> vertices, net.minecraft.client.model.geom.ModelPart vanillaModelPart) {
		this.vertices = vertices;
		this.vanillaModelPart = vanillaModelPart;
	}
	
	public void setVanillaTransfrom() {
		if (this.vanillaModelPart != null) {
			
		}
	}
	
	public List<T> getVertices() {
		return this.vertices;
	}
}