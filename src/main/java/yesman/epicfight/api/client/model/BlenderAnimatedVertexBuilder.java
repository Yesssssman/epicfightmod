package yesman.epicfight.api.client.model;

import org.joml.Vector3i;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlenderAnimatedVertexBuilder extends BlenderVertexBuilder {
	public final Vector3i joint;
	public final Vector3i weight;
	public final int count;
	
	public BlenderAnimatedVertexBuilder(int position, int uv, int normal, Vector3i joint, Vector3i weight, int count) {
		super(position, uv, normal);
		
		this.joint = joint;
		this.weight = weight;
		this.count = count;
	}
}