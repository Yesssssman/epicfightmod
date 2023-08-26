package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class IronGolemMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> head;
	public final ModelPart<AnimatedVertexIndicator> chest;
	public final ModelPart<AnimatedVertexIndicator> core;
	public final ModelPart<AnimatedVertexIndicator> leftArm;
	public final ModelPart<AnimatedVertexIndicator> rightArm;
	public final ModelPart<AnimatedVertexIndicator> leftLeg;
	public final ModelPart<AnimatedVertexIndicator> rightLeg;
	
	public IronGolemMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.head = this.getOrLogException(parts, "head");
		this.chest = this.getOrLogException(parts, "chest");
		this.core = this.getOrLogException(parts, "core");
		this.leftArm = this.getOrLogException(parts, "leftArm");
		this.rightArm = this.getOrLogException(parts, "rightArm");
		this.leftLeg = this.getOrLogException(parts, "leftLeg");
		this.rightLeg = this.getOrLogException(parts, "rightLeg");
	}
}