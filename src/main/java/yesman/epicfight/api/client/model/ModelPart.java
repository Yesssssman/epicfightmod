package yesman.epicfight.api.client.model;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPart<T extends VertexIndicator> {
	private final List<T> vertices;
	public boolean hidden;
	
	public ModelPart(List<T> vertices) {
		this.vertices = vertices;
	}
	
	public List<T> getVertices() {
		return this.vertices;
	}
}