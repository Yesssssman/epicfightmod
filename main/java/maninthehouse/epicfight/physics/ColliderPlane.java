package maninthehouse.epicfight.physics;

import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;

public class ColliderPlane extends Collider {
	private Vec3f[] modelPos;
	private Vec3f[] worldPos;

	public ColliderPlane(Vec3f center, AxisAlignedBB entityCallAABB) {
		super(center, entityCallAABB);
	}
	
	public ColliderPlane(AxisAlignedBB entityCallAABB, float centerX, float centerY, float centerZ, float pos1X, float pos1Y, float pos1Z, float pos2X, float pos2Y, float pos2Z) {
		super(new Vec3f(centerX, centerY, centerZ), entityCallAABB);
		
		modelPos = new Vec3f[2];
		worldPos = new Vec3f[2];
		
		modelPos[0] = new Vec3f(pos1X, pos1Y, pos1Z);
		modelPos[1] = new Vec3f(pos2X, pos2Y, pos2Z);
		
		worldPos[0] = new Vec3f();
		worldPos[1] = new Vec3f();
	}

	@Override
	public boolean isCollideWith(Entity entity) {
		AxisAlignedBB opponent = entity.getEntityBoundingBox();
		Vec3f planeNorm = Vec3f.cross(worldPos[0], worldPos[1], null);
		Vec3f pos = new Vec3f();
		Vec3f neg = new Vec3f();
		
		if (planeNorm.x >= 0) {
			pos.x = (float) opponent.maxX;
			neg.x = (float) opponent.minX;
		} else {
			pos.x = (float) opponent.minX;
			neg.x = (float) opponent.maxX;
		}
		if (planeNorm.y >= 0) {
			pos.y = (float) opponent.maxY;
			neg.y = (float) opponent.minY;
		} else {
			pos.y = (float) opponent.minY;
			neg.y = (float) opponent.maxY;
		}
		if (planeNorm.z >= 0) {
			pos.z = (float) opponent.maxZ;
			neg.z = (float) opponent.minZ;
		} else {
			pos.z = (float) opponent.minZ;
			neg.z = (float) opponent.maxZ;
		}
		
		float planeD = Vec3f.dot(planeNorm, worldCenter);
		float dot1 = Vec3f.dot(planeNorm, pos) - planeD;

		if (dot1 < 0) {
			return false;
		}

		float dot2 = Vec3f.dot(planeNorm, neg) - planeD;

		if (dot2 > 0) {
			return false;
		}
		
		return true;
	}

	@Override
	public void transform(VisibleMatrix4f mat) {
		Vec4f tempVector = new Vec4f(0,0,0,1);
		VisibleMatrix4f rotationMatrix = new VisibleMatrix4f(mat);
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;

		for (int i = 0; i < 2; i++) {
			tempVector.x = modelPos[i].x;
			tempVector.y = modelPos[i].y;
			tempVector.z = modelPos[i].z;
			VisibleMatrix4f.transform(rotationMatrix, tempVector, tempVector);
			worldPos[i].x = tempVector.x;
			worldPos[i].y = tempVector.y;
			worldPos[i].z = tempVector.z;
		}
		
		super.transform(mat);
	}
	
	@Override
	public void draw(VisibleMatrix4f pose, float partialTicks, boolean red) {
		
	}
}