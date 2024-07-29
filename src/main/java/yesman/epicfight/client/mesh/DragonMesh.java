package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshProvider;

@OnlyIn(Dist.CLIENT)
public class DragonMesh extends AnimatedMesh implements MeshProvider<DragonMesh> {
	public final AnimatedModelPart head;
	public final AnimatedModelPart neck;
	public final AnimatedModelPart torso;
	public final AnimatedModelPart leftLegFront;
	public final AnimatedModelPart rightLegFront;
	public final AnimatedModelPart leftLegBack;
	public final AnimatedModelPart rightLegBack;
	public final AnimatedModelPart leftWing;
	public final AnimatedModelPart rightWing;
	public final AnimatedModelPart tail;
	
	public DragonMesh(Map<String, float[]> arrayMap, Map<String, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.neck = this.getOrLogException(this.parts, "neck");
		this.torso = this.getOrLogException(this.parts, "torso");
		this.leftLegFront = this.getOrLogException(this.parts, "leftLegFront");
		this.rightLegFront = this.getOrLogException(this.parts, "rightLegFront");
		this.leftLegBack = this.getOrLogException(this.parts, "leftLegBack");
		this.rightLegBack = this.getOrLogException(this.parts, "rightLegBack");
		this.leftWing = this.getOrLogException(this.parts, "leftWing");
		this.rightWing = this.getOrLogException(this.parts, "rightWing");
		this.tail = this.getOrLogException(this.parts, "tail");
	}

	@Override
	public DragonMesh get() {
		return this;
	}
}