package maninthehouse.epicfight.physics;

import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class ColliderLine extends Collider {
	private Vec3f modelVec;
	private Vec3f worldVec;
	
	public ColliderLine(AxisAlignedBB entityCallAABB, float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		super(new Vec3f(posX, posY, posZ), entityCallAABB);
		this.modelVec = new Vec3f(vecX, vecY, vecZ);
		this.worldVec = new Vec3f();
	}
	
	@Override
	public void transform(VisibleMatrix4f mat) {
		Vec4f tempVector = new Vec4f(0,0,0,1);
		VisibleMatrix4f rotationMatrix = new VisibleMatrix4f(mat);
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		
		tempVector.x = modelVec.x;
		tempVector.y = modelVec.y;
		tempVector.z = modelVec.z;
		VisibleMatrix4f.transform(rotationMatrix, tempVector, tempVector);
		worldVec.x = tempVector.x;
		worldVec.y = tempVector.y;
		worldVec.z = tempVector.z;
		
		super.transform(mat);
	}

	public boolean isCollideWith(Entity entity) {
		AxisAlignedBB opponent = entity.getEntityBoundingBox();
		
		float maxStart;
		float minEnd;
		
		float startX;
		float startY;
		float startZ;
		float endX;
		float endY;
		float endZ;
		
		if (worldVec.x == 0) {
			if (worldCenter.x < opponent.minX || worldCenter.x > opponent.maxX) {
				return false;
			}
		}

		startX = MathHelper.clamp((float) (opponent.minX + worldCenter.x) / -worldVec.x, 0, 1);
		endX = MathHelper.clamp((float) (opponent.maxX + worldCenter.x) / -worldVec.x, 0, 1);

		if (startX > endX) {
			float temp = startX;
			startX = endX;
			endX = temp;
		}
		
		maxStart = startX;
		minEnd = endX;

		if (minEnd == maxStart) {
			return false;
		}

		if (worldVec.y == 0) {
			if (worldCenter.y < opponent.minY || worldCenter.y > opponent.maxY) {
				return false;
			}
		}

		startY = MathHelper.clamp((float) (opponent.minY - worldCenter.y) / worldVec.y, 0, 1);
		endY = MathHelper.clamp((float) (opponent.maxY - worldCenter.y) / worldVec.y, 0, 1);

		if (startY > endY) {
			float temp = startY;
			startY = endY;
			endY = temp;
		}
		
		maxStart = maxStart < startY ? startY : maxStart;
		minEnd = minEnd > endY ? endY : minEnd;
		
		if (maxStart >= minEnd) {
			return false;
		}

		if (worldVec.z == 0) {
			if (worldCenter.z < opponent.minZ || worldCenter.z > opponent.maxZ) {
				return false;
			}
		}
		
		startZ = MathHelper.clamp((float)(opponent.minZ + worldCenter.z) / -worldVec.z, 0, 1);
		endZ = MathHelper.clamp((float)(opponent.maxZ + worldCenter.z) / -worldVec.z, 0, 1);

		if (startZ > endZ) {
			float temp = startZ;
			startZ = endZ;
			endZ = temp;
		}

		maxStart = maxStart < startZ ? startZ : maxStart;
		minEnd = minEnd > endZ ? endZ : minEnd;

		if (maxStart >= minEnd) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void draw(VisibleMatrix4f pose, float partialTicks, boolean red) {
		
	}
}