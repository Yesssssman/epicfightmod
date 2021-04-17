package maninthehouse.epicfight.utils.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtils {
	public static VisibleMatrix4f getModelMatrixIntegrated(float prevPosX, float posX, float prevPosY, float posY,
			float prevPosZ, float posZ, float prevPitch, float pitch, float prevYaw, float yaw, float partialTick,
			float scaleX, float scaleY, float scaleZ) {
		VisibleMatrix4f modelMatrix = new VisibleMatrix4f().setIdentity();
		Vec3f entityPosition = new Vec3f(-(prevPosX + (posX - prevPosX) * partialTick),
				((prevPosY + (posY - prevPosY) * partialTick)), -(prevPosZ + (posZ - prevPosZ) * partialTick));
		
		VisibleMatrix4f.translate(entityPosition, modelMatrix, modelMatrix);
		float pitchDegree = interpolateRotation(prevPitch, pitch, partialTick);
		float yawDegree = interpolateRotation(prevYaw, yaw, partialTick);
		VisibleMatrix4f.rotate((float) -Math.toRadians(yawDegree), new Vec3f(0, 1, 0), modelMatrix, modelMatrix);
		VisibleMatrix4f.rotate((float) -Math.toRadians(pitchDegree), new Vec3f(1, 0, 0), modelMatrix, modelMatrix);
		VisibleMatrix4f.scale(new Vec3f(scaleX, scaleY, scaleZ), modelMatrix, modelMatrix);
		
		return modelMatrix;
	}

	public static Vec3d getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = MathHelper.cos(f1);
		float f3 = MathHelper.sin(f1);
		float f4 = MathHelper.cos(f);
		float f5 = MathHelper.sin(f);

		return new Vec3d((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}

	public static float interpolateRotation(float par1, float par2, float par3) {
		float f = 0;

		for (f = par2 - par1; f < -180.0F; f += 360.0F) {
			;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return par1 + par3 * f;
	}

	public static float getInterpolatedRotation(float par1, float par2, float par3) {
		float f = 0;

		for (f = par2 - par1; f < -180.0F; f += 360.0F) {
			;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return par3 * f;
	}

	public static double getAngleBetween(Entity e1, Entity e2) {
		Vec3d a = e1.getLookVec();
		Vec3d b = new Vec3d(e2.posX - e1.posX, e2.posY - e1.posY, e2.posZ - e1.posZ).normalize();
		double cosTheta = (a.x * b.x + a.y * b.y + a.z * b.z);
		return Math.acos(cosTheta);
	}

	public static double lerp(double pct, double start, double end) {
		return start + pct * (end - start);
	}
}