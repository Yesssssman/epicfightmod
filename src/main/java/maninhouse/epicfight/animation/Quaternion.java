package maninhouse.epicfight.animation;

import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec3f;

public class Quaternion {
	private float x, y, z, w;

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		normalize();
	}

	public void normalize() {
		float mag = (float) Math.sqrt(w * w + x * x + y * y + z * z);
		w /= mag;
		x /= mag;
		y /= mag;
		z /= mag;
	}

	public OpenMatrix4f toRotationMatrix() {
		OpenMatrix4f matrix = new OpenMatrix4f();
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yz = y * z;
		final float yw = y * w;
		final float zw = z * w;
		final float xSquared = 2F * x * x;
		final float ySquared = 2F * y * y;
		final float zSquared = 2F * z * z;
		matrix.m00 = 1.0F - ySquared - zSquared;
		matrix.m01 = 2.0F * (xy - zw);
		matrix.m02 = 2.0F * (xz + yw);
		matrix.m10 = 2.0F * (xy + zw);
		matrix.m11 = 1.0F - xSquared - zSquared;
		matrix.m12 = 2.0F * (yz - xw);
		matrix.m20 = 2.0F * (xz - yw);
		matrix.m21 = 2.0F * (yz + xw);
		matrix.m22 = 1.0F - xSquared - ySquared;
		return matrix;
	}

	public static Quaternion fromMatrix(OpenMatrix4f matrix) {
		float w, x, y, z;
		float diagonal = matrix.m00 + matrix.m11 + matrix.m22;
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			x = (matrix.m21 - matrix.m12) / w4;
			y = (matrix.m02 - matrix.m20) / w4;
			z = (matrix.m10 - matrix.m01) / w4;
		} else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22)) {
			float x4 = (float) (Math.sqrt(1f + matrix.m00 - matrix.m11 - matrix.m22) * 2f);
			w = (matrix.m21 - matrix.m12) / x4;
			x = x4 / 4f;
			y = (matrix.m01 + matrix.m10) / x4;
			z = (matrix.m02 + matrix.m20) / x4;
		} else if (matrix.m11 > matrix.m22) {
			float y4 = (float) (Math.sqrt(1f + matrix.m11 - matrix.m00 - matrix.m22) * 2f);
			w = (matrix.m02 - matrix.m20) / y4;
			x = (matrix.m01 + matrix.m10) / y4;
			y = y4 / 4f;
			z = (matrix.m12 + matrix.m21) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + matrix.m22 - matrix.m00 - matrix.m11) * 2f);
			w = (matrix.m10 - matrix.m01) / z4;
			x = (matrix.m02 + matrix.m20) / z4;
			y = (matrix.m12 + matrix.m21) / z4;
			z = z4 / 4f;
		}
		return new Quaternion(x, y, z, w);
	}

	public static Quaternion interpolate(Quaternion a, Quaternion b, float blend) {
		Quaternion result = new Quaternion(0, 0, 0, 1);
		float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
		float blendI = 1f - blend;

		if (dot < 0) {
			result.w = blendI * a.w + blend * -b.w;
			result.x = blendI * a.x + blend * -b.x;
			result.y = blendI * a.y + blend * -b.y;
			result.z = blendI * a.z + blend * -b.z;
		} else {
			result.w = blendI * a.w + blend * b.w;
			result.x = blendI * a.x + blend * b.x;
			result.y = blendI * a.y + blend * b.y;
			result.z = blendI * a.z + blend * b.z;
		}
		
		result.normalize();
		return result;
	}

	public static Quaternion rotate(float degree, Vec3f axis, Quaternion src) {
		OpenMatrix4f quatmat;
		if (src == null) {
			quatmat = new OpenMatrix4f();
		} else {
			quatmat = src.toRotationMatrix();
		}
		
		OpenMatrix4f rotMat = new OpenMatrix4f();
		OpenMatrix4f.rotate(degree, axis, rotMat, rotMat);
		OpenMatrix4f.mul(quatmat, rotMat,  quatmat);
		return Quaternion.fromMatrix(quatmat);
	}
	
	@Override
	public String toString() {
		return String.format("%f %f %f %f", this.w, this.x, this.y, this.z);
	}
}
