package yesman.epicfight.api.client.model;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Mesh {
	final float[] positions;
	final float[] noramls;
	final float[] uvs;
	final float[] weights;
	final List<VertexIndicator> vertexIndicators;
	
	public Mesh(float[] positions, float[] noramls, float[] uvs, int[] weightIndices, float[] weights, int[] indices, int[] vCounts) {
		this.positions = positions;
		this.noramls = noramls;
		this.uvs = uvs;
		this.weights = weights;
		this.vertexIndicators = VertexIndicator.create(indices, vCounts, weightIndices);
	}
}