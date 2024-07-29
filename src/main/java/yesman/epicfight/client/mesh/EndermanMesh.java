package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class EndermanMesh extends AnimatedMesh implements MeshProvider<EndermanMesh> {
	public final AnimatedModelPart headTop;
	public final AnimatedModelPart headBottom;
	public final AnimatedModelPart torso;
	public final AnimatedModelPart leftArm;
	public final AnimatedModelPart rightArm;
	public final AnimatedModelPart leftLeg;
	public final AnimatedModelPart rightLeg;
	
	public EndermanMesh(Map<String, float[]> arrayMap, Map<String, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.headTop = this.getOrLogException(this.parts, "headTop");
		this.headBottom = this.getOrLogException(this.parts, "headBottom");
		this.torso = this.getOrLogException(this.parts, "torso");
		this.leftArm = this.getOrLogException(this.parts, "leftArm");
		this.rightArm = this.getOrLogException(this.parts, "rightArm");
		this.leftLeg = this.getOrLogException(this.parts, "leftLeg");
		this.rightLeg = this.getOrLogException(this.parts, "rightLeg");
	}
	
	@Override
	public EndermanMesh get() {
		return this;
	}
}