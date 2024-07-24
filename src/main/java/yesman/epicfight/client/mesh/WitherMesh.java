package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;

@OnlyIn(Dist.CLIENT)
public class WitherMesh extends AnimatedMesh {
	public final AnimatedModelPart centerHead;
	public final AnimatedModelPart leftHead;
	public final AnimatedModelPart rightHead;
	public final AnimatedModelPart ribcage;
	public final AnimatedModelPart tail;
	
	public WitherMesh(Map<String, float[]> arrayMap, Map<String, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.centerHead = this.getOrLogException(this.parts, "centerHead");
		this.leftHead = this.getOrLogException(this.parts, "leftHead");
		this.rightHead = this.getOrLogException(this.parts, "rightHead");
		this.ribcage = this.getOrLogException(this.parts, "ribcage");
		this.tail = this.getOrLogException(this.parts, "tail");
	}
}