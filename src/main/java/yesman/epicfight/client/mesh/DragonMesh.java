package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class DragonMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> head;
	public final ModelPart<AnimatedVertexIndicator> neck;
	public final ModelPart<AnimatedVertexIndicator> torso;
	public final ModelPart<AnimatedVertexIndicator> leftLegFront;
	public final ModelPart<AnimatedVertexIndicator> rightLegFront;
	public final ModelPart<AnimatedVertexIndicator> leftLegBack;
	public final ModelPart<AnimatedVertexIndicator> rightLegBack;
	public final ModelPart<AnimatedVertexIndicator> leftWing;
	public final ModelPart<AnimatedVertexIndicator> rightWing;
	public final ModelPart<AnimatedVertexIndicator> tail;
	
	public DragonMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.head = this.getOrLogException(parts, "head");
		this.neck = this.getOrLogException(parts, "neck");
		this.torso = this.getOrLogException(parts, "torso");
		this.leftLegFront = this.getOrLogException(parts, "leftLegFront");
		this.rightLegFront = this.getOrLogException(parts, "rightLegFront");
		this.leftLegBack = this.getOrLogException(parts, "leftLegBack");
		this.rightLegBack = this.getOrLogException(parts, "rightLegBack");
		this.leftWing = this.getOrLogException(parts, "leftWing");
		this.rightWing = this.getOrLogException(parts, "rightWing");
		this.tail = this.getOrLogException(parts, "tail");
	}
}