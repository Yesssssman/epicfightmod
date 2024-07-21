package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.BlenderAnimatedVertexBuilder;

@OnlyIn(Dist.CLIENT)
public class CreeperMesh extends AnimatedMesh {
	public final AnimatedModelPart head;
	public final AnimatedModelPart torso;
	public final AnimatedModelPart legRF;
	public final AnimatedModelPart legLF;
	public final AnimatedModelPart legRB;
	public final AnimatedModelPart legLB;
	
	public CreeperMesh(Map<String, float[]> arrayMap, Map<String, List<BlenderAnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.torso = this.getOrLogException(this.parts, "torso");
		this.legRF = this.getOrLogException(this.parts, "legRF");
		this.legLF = this.getOrLogException(this.parts, "legLF");
		this.legRB = this.getOrLogException(this.parts, "legRB");
		this.legLB = this.getOrLogException(this.parts, "legLB");
	}
}