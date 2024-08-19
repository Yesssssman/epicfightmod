package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class RavagerMesh extends AnimatedMesh implements MeshProvider<RavagerMesh> {
	public final AnimatedModelPart head;
	public final AnimatedModelPart body;
	public final AnimatedModelPart leftFrontLeg;
	public final AnimatedModelPart rightFrontLeg;
	public final AnimatedModelPart leftBackLeg;
	public final AnimatedModelPart rightBackLeg;
	
	public RavagerMesh(Map<String, float[]> arrayMap, Map<MeshPartDefinition, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.body = this.getOrLogException(this.parts, "body");
		this.leftFrontLeg = this.getOrLogException(this.parts, "leftFrontLeg");
		this.rightFrontLeg = this.getOrLogException(this.parts, "rightFrontLeg");
		this.leftBackLeg = this.getOrLogException(this.parts, "leftBackLeg");
		this.rightBackLeg = this.getOrLogException(this.parts, "rightBackLeg");
	}

	@Override
	public RavagerMesh get() {
		return this;
	}
}