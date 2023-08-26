package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class SpiderMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> head;
	public final ModelPart<AnimatedVertexIndicator> middleStomach;
	public final ModelPart<AnimatedVertexIndicator> bottomStomach;
	public final ModelPart<AnimatedVertexIndicator> leftLeg1;
	public final ModelPart<AnimatedVertexIndicator> leftLeg2;
	public final ModelPart<AnimatedVertexIndicator> leftLeg3;
	public final ModelPart<AnimatedVertexIndicator> leftLeg4;
	public final ModelPart<AnimatedVertexIndicator> rightLeg1;
	public final ModelPart<AnimatedVertexIndicator> rightLeg2;
	public final ModelPart<AnimatedVertexIndicator> rightLeg3;
	public final ModelPart<AnimatedVertexIndicator> rightLeg4;
	
	public SpiderMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.head = this.getOrLogException(parts, "head");
		this.middleStomach = this.getOrLogException(parts, "middleStomach");
		this.bottomStomach = this.getOrLogException(parts, "bottomStomach");
		this.leftLeg1 = this.getOrLogException(parts, "leftLeg1");
		this.leftLeg2 = this.getOrLogException(parts, "leftLeg2");
		this.leftLeg3 = this.getOrLogException(parts, "leftLeg3");
		this.leftLeg4 = this.getOrLogException(parts, "leftLeg4");
		this.rightLeg1 = this.getOrLogException(parts, "rightLeg1");
		this.rightLeg2 = this.getOrLogException(parts, "rightLeg2");
		this.rightLeg3 = this.getOrLogException(parts, "rightLeg3");
		this.rightLeg4 = this.getOrLogException(parts, "rightLeg4");
	}
}