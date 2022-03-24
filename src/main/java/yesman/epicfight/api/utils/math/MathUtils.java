package yesman.epicfight.api.utils.math;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtils {
	public static OpenMatrix4f getModelMatrixIntegral(float prevPosX, float posX, float prevPosY, float posY,
			float prevPosZ, float posZ, float prevPitch, float pitch, float prevYaw, float yaw, float partialTick,
			float scaleX, float scaleY, float scaleZ) {
		OpenMatrix4f modelMatrix = new OpenMatrix4f();
		Vec3f entityPosition = new Vec3f(-(prevPosX + (posX - prevPosX) * partialTick), ((prevPosY + (posY - prevPosY) * partialTick)), -(prevPosZ + (posZ - prevPosZ) * partialTick));
		float pitchDegree = lerpBetween(prevPitch, pitch, partialTick);
		float yawDegree = lerpBetween(prevYaw, yaw, partialTick);
		modelMatrix.translate(entityPosition).rotateDeg(-yawDegree, Vec3f.Y_AXIS).rotateDeg(-pitchDegree, Vec3f.X_AXIS).scale(scaleX, scaleY, scaleZ);
		return modelMatrix;
	}

	public static Vec3 getVectorForRotation(float pitch, float yaw) {
		float f = pitch * ((float) Math.PI / 180F);
		float f1 = -yaw * ((float) Math.PI / 180F);
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Mth.cos(f);
		float f5 = Mth.sin(f);

		return new Vec3((double) (f3 * f4), (double) (-f5), (double) (f2 * f4));
	}
	
	public static float lerpBetween(float f1, float f2, float zero2one) {
		float f = 0;

		for (f = f2 - f1; f < -180.0F; f += 360.0F) {
			;
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return f1 + zero2one * f;
	}
	
	public static float rotlerp(float from, float to, float limit) {
		float f = Mth.wrapDegrees(to - from);
		
		if (f > limit) {
			f = limit;
		}
		
		if (f < -limit) {
			f = -limit;
		}
		
		float f1 = from + f;
		
		while (f1 >= 180.0F) {
			f1 -= 360.0F;
		}
		
		while (f1 <= -180.0F) {
			f1 += 360.0F;
		}
		
		return f1;
	}
	
	public static void translateStack(PoseStack mStack, OpenMatrix4f mat) {
		Vector3f vector = new Vector3f(mat.m30, mat.m31, mat.m32);
		mStack.translate(vector.x(), vector.y(), vector.z());
	}
	
	public static void rotateStack(PoseStack mStack, OpenMatrix4f mat) {
		mStack.mulPose(getQuaternionFromMatrix(mat));
	}
	
	public static void scaleStack(PoseStack mStack, OpenMatrix4f mat) {
		Vector3f vector = getScaleVectorFromMatrix(mat);
		mStack.scale(vector.x(), vector.y(), vector.z());
	}
	
	public static double getAngleBetween(Vec3f a, Vec3f b) {
		double cos = (a.x * b.x + a.y * b.y + a.z * b.z);
		return Math.acos(cos);
	}
	
	private static Quaternion getQuaternionFromMatrix(OpenMatrix4f mat) {
		float w, x, y, z;
		float diagonal = mat.m00 + mat.m11 + mat.m22;

		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1.0F) * 2.0F);
			w = w4 * 0.25F;
			x = (mat.m21 - mat.m12) / w4;
			y = (mat.m02 - mat.m20) / w4;
			z = (mat.m10 - mat.m01) / w4;
		} else if ((mat.m00 > mat.m11) && (mat.m00 > mat.m22)) {
			float x4 = (float) (Math.sqrt(1.0F + mat.m00 - mat.m11 - mat.m22) * 2F);
			w = (mat.m21 - mat.m12) / x4;
			x = x4 * 0.25F;
			y = (mat.m01 + mat.m10) / x4;
			z = (mat.m02 + mat.m20) / x4;
		} else if (mat.m11 > mat.m22) {
			float y4 = (float) (Math.sqrt(1.0F + mat.m11 - mat.m00 - mat.m22) * 2F);
			w = (mat.m02 - mat.m20) / y4;
			x = (mat.m01 + mat.m10) / y4;
			y = y4 * 0.25F;
			z = (mat.m12 + mat.m21) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1.0F + mat.m22 - mat.m00 - mat.m11) * 2F);
			w = (mat.m10 - mat.m01) / z4;
			x = (mat.m02 + mat.m20) / z4;
			y = (mat.m12 + mat.m21) / z4;
			z = z4 * 0.25F;
		}
		
		Quaternion quat = new Quaternion(x, y, z, w);
		quat.normalize();
		return quat;
	}
	
	public static Vec3f lerpVector(Vec3f start, Vec3f end, float weight) {
		float x = start.x + (end.x - start.x) * weight;
		float y = start.y + (end.y - start.y) * weight;
		float z = start.z + (end.z - start.z) * weight;
		return new Vec3f(x, y, z);
	}
	
	public static void setQuaternion(Quaternion quat, float x, float y, float z, float w) {
		quat.i = x;
		quat.j = y;
		quat.k = z;
		quat.r = w;
	}
	
	public static Quaternion mulQuaternion(Quaternion left, Quaternion right, Quaternion dest) {
		if (dest == null) {
			dest = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		}
		
		float f = left.i();
	    float f1 = left.j();
	    float f2 = left.k();
	    float f3 = left.r();
	    float f4 = right.i();
	    float f5 = right.j();
	    float f6 = right.k();
	    float f7 = right.r();
	    float i = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
	    float j = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
	    float k = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
	    float r = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
	    
	    dest.set(i, j, k, r);
	    
	    return dest;
	}
	
	public static Quaternion lerpQuaternion(Quaternion from, Quaternion to, float weight) {
		float fromX = from.i();
		float fromY = from.j();
		float fromZ = from.k();
		float fromW = from.r();
		float toX = to.i();
		float toY = to.j();
		float toZ = to.k();
		float toW = to.r();
		float resultX;
		float resultY;
		float resultZ;
		float resultW;
		float dot = fromW * toW + fromX * toX + fromY * toY + fromZ * toZ;
		float blendI = 1f - weight;
		
		if (dot < 0) {
			resultW = blendI * fromW + weight * -toW;
			resultX = blendI * fromX + weight * -toX;
			resultY = blendI * fromY + weight * -toY;
			resultZ = blendI * fromZ + weight * -toZ;
		} else {
			resultW = blendI * fromW + weight * toW;
			resultX = blendI * fromX + weight * toX;
			resultY = blendI * fromY + weight * toY;
			resultZ = blendI * fromZ + weight * toZ;
		}
		
		Quaternion result = new Quaternion(resultX, resultY, resultZ, resultW);
		normalizeQuaternion(result);
		return result;
	}
	
	private static void normalizeQuaternion(Quaternion quaternion) {
		float f = quaternion.i() * quaternion.i() + quaternion.j() * quaternion.j() + quaternion.k() * quaternion.k() + quaternion.r() * quaternion.r();
		if (f > 1.0E-6F) {
			float f1 = fastInvSqrt(f);
			setQuaternion(quaternion, quaternion.i() * f1, quaternion.j() * f1, quaternion.k() * f1, quaternion.r() * f1);
		} else {
			setQuaternion(quaternion, 0.0F, 0.0F, 0.0F, 0.0F);
		}
	}
	
	private static Vector3f getScaleVectorFromMatrix(OpenMatrix4f mat) {
		Vec3f a = new Vec3f(mat.m00, mat.m10, mat.m20);
		Vec3f b = new Vec3f(mat.m01, mat.m11, mat.m21);
		Vec3f c = new Vec3f(mat.m02, mat.m12, mat.m22);
		return new Vector3f(a.length(), b.length(), c.length());
	}
	
	private static float fastInvSqrt(float number) {
		float f = 0.5F * number;
		int i = Float.floatToIntBits(number);
		i = 1597463007 - (i >> 1);
		number = Float.intBitsToFloat(i);
		return number * (1.5F - f * number * number);
	}
}