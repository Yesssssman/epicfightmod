package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class HoglinMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> head;
	public final ModelPart<AnimatedVertexIndicator> body;
	public final ModelPart<AnimatedVertexIndicator> leftFrontLeg;
	public final ModelPart<AnimatedVertexIndicator> rightFrontLeg;
	public final ModelPart<AnimatedVertexIndicator> leftBackLeg;
	public final ModelPart<AnimatedVertexIndicator> rightBackLeg;
	
	public HoglinMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.head = this.getOrLogException(parts, "head");
		this.body = this.getOrLogException(parts, "body");
		this.leftFrontLeg = this.getOrLogException(parts, "leftFrontLeg");
		this.rightFrontLeg = this.getOrLogException(parts, "rightFrontLeg");
		this.leftBackLeg = this.getOrLogException(parts, "leftBackLeg");
		this.rightBackLeg = this.getOrLogException(parts, "rightBackLeg");
	}
}