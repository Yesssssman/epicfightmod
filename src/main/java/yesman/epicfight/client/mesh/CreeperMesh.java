package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class CreeperMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> head;
	public final ModelPart<AnimatedVertexIndicator> torso;
	public final ModelPart<AnimatedVertexIndicator> legRF;
	public final ModelPart<AnimatedVertexIndicator> legLF;
	public final ModelPart<AnimatedVertexIndicator> legRB;
	public final ModelPart<AnimatedVertexIndicator> legLB;
	
	public CreeperMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.head = this.getOrLogException(parts, "head");
		this.torso = this.getOrLogException(parts, "torso");
		this.legRF = this.getOrLogException(parts, "legRF");
		this.legLF = this.getOrLogException(parts, "legLF");
		this.legRB = this.getOrLogException(parts, "legRB");
		this.legLB = this.getOrLogException(parts, "legLB");
	}
}