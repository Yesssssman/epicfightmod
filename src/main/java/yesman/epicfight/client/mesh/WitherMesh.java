package yesman.epicfight.client.mesh;

import java.util.Map;

import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class WitherMesh extends AnimatedMesh {
	public final ModelPart<AnimatedVertexIndicator> centerHead;
	public final ModelPart<AnimatedVertexIndicator> leftHead;
	public final ModelPart<AnimatedVertexIndicator> rightHead;
	public final ModelPart<AnimatedVertexIndicator> ribcage;
	public final ModelPart<AnimatedVertexIndicator> tail;
	
	public WitherMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
		
		this.centerHead = this.getOrLogException(parts, "centerHead");
		this.leftHead = this.getOrLogException(parts, "leftHead");
		this.rightHead = this.getOrLogException(parts, "rightHead");
		this.ribcage = this.getOrLogException(parts, "ribcage");
		this.tail = this.getOrLogException(parts, "tail");
	}
}