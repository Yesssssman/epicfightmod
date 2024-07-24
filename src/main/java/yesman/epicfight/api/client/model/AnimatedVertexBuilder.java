package yesman.epicfight.api.client.model;

import org.joml.Vector3i;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimatedVertexBuilder extends VertexBuilder {
	public final Vector3i joint;
	public final Vector3i weight;
	public final int count;
	
	public AnimatedVertexBuilder(int position, int uv, int normal, Vector3i joint, Vector3i weight, int count) {
		super(position, uv, normal);
		
		this.joint = joint;
		this.weight = weight;
		this.count = count;
	}
	
	public int getJointId(int index) {
		switch (index) {
		case 0:
			return this.joint.x;
		case 1:
			return this.joint.y;
		case 2:
			return this.joint.z;
		default:
			return -1;
		}
	}
	
	public int getWeightIndex(int index) {
		switch (index) {
		case 0:
			return this.weight.x;
		case 1:
			return this.weight.y;
		case 2:
			return this.weight.z;
		default:
			return -1;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof AnimatedVertexBuilder vb) {
			return this.position == vb.position && this.uv == vb.uv && this.normal == vb.normal && this.count == vb.count && this.joint.x == vb.joint.x && this.joint.y == vb.joint.y && this.joint.z == vb.joint.z
					&& this.weight.x == vb.weight.x && this.weight.y == vb.weight.y && this.weight.z == vb.weight.z;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        
        result = prime * result + this.position;
        result = prime * result + this.uv;
        result = prime * result + this.normal;
        result = prime * result + this.count;
        result = prime * result + this.joint.x;
        result = prime * result + this.joint.y;
        result = prime * result + this.joint.z;
        result = prime * result + this.weight.x;
        result = prime * result + this.weight.y;
        result = prime * result + this.weight.z;
        
        return result;
    }
}