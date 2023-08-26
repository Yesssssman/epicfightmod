package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class EndermanMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> headTop;
	public final ModelPart<AnimatedVertexIndicator> headBottom;
	public final ModelPart<AnimatedVertexIndicator> torso;
	public final ModelPart<AnimatedVertexIndicator> leftArm;
	public final ModelPart<AnimatedVertexIndicator> rightArm;
	public final ModelPart<AnimatedVertexIndicator> leftLeg;
	public final ModelPart<AnimatedVertexIndicator> rightLeg;
	
	public EndermanMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.headTop = this.getOrLogException(parts, "headTop");
		this.headBottom = this.getOrLogException(parts, "headBottom");
		this.torso = this.getOrLogException(parts, "torso");
		this.leftArm = this.getOrLogException(parts, "leftArm");
		this.rightArm = this.getOrLogException(parts, "rightArm");
		this.leftLeg = this.getOrLogException(parts, "leftLeg");
		this.rightLeg = this.getOrLogException(parts, "rightLeg");
	}
}