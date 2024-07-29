package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class IronGolemMesh extends AnimatedMesh implements MeshProvider<IronGolemMesh> {
	public final AnimatedModelPart head;
	public final AnimatedModelPart chest;
	public final AnimatedModelPart core;
	public final AnimatedModelPart leftArm;
	public final AnimatedModelPart rightArm;
	public final AnimatedModelPart leftLeg;
	public final AnimatedModelPart rightLeg;
	
	public IronGolemMesh(Map<String, float[]> arrayMap, Map<String, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.chest = this.getOrLogException(this.parts, "chest");
		this.core = this.getOrLogException(this.parts, "core");
		this.leftArm = this.getOrLogException(this.parts, "leftArm");
		this.rightArm = this.getOrLogException(this.parts, "rightArm");
		this.leftLeg = this.getOrLogException(this.parts, "leftLeg");
		this.rightLeg = this.getOrLogException(this.parts, "rightLeg");
	}

	@Override
	public IronGolemMesh get() {
		return this;
	}
}