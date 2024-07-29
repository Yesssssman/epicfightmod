package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class SpiderMesh extends AnimatedMesh implements MeshProvider<SpiderMesh> {
	public final AnimatedModelPart head;
	public final AnimatedModelPart middleStomach;
	public final AnimatedModelPart bottomStomach;
	public final AnimatedModelPart leftLeg1;
	public final AnimatedModelPart leftLeg2;
	public final AnimatedModelPart leftLeg3;
	public final AnimatedModelPart leftLeg4;
	public final AnimatedModelPart rightLeg1;
	public final AnimatedModelPart rightLeg2;
	public final AnimatedModelPart rightLeg3;
	public final AnimatedModelPart rightLeg4;
	
	public SpiderMesh(Map<String, float[]> arrayMap, Map<String, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.middleStomach = this.getOrLogException(this.parts, "middleStomach");
		this.bottomStomach = this.getOrLogException(this.parts, "bottomStomach");
		this.leftLeg1 = this.getOrLogException(this.parts, "leftLeg1");
		this.leftLeg2 = this.getOrLogException(this.parts, "leftLeg2");
		this.leftLeg3 = this.getOrLogException(this.parts, "leftLeg3");
		this.leftLeg4 = this.getOrLogException(this.parts, "leftLeg4");
		this.rightLeg1 = this.getOrLogException(this.parts, "rightLeg1");
		this.rightLeg2 = this.getOrLogException(this.parts, "rightLeg2");
		this.rightLeg3 = this.getOrLogException(this.parts, "rightLeg3");
		this.rightLeg4 = this.getOrLogException(this.parts, "rightLeg4");
	}
	
	@Override
	public SpiderMesh get() {
		return this;
	}
}