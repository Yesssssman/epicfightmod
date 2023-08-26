package yesman.epicfight.api.utils.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;

import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.JointTransform;

public class OpenMatrix4f {
	public static class AnimationTransformEntry {
		private static final String[] BINDING_PRIORITY = {JointTransform.PARENT, JointTransform.JOINT_LOCAL_TRANSFORM, JointTransform.ANIMATION_TRANSFROM, JointTransform.RESULT1, JointTransform.RESULT2};
		private Map<String, Pair<OpenMatrix4f, MatrixOperation>> matrices = Maps.newHashMap();
		
		public void put(String entryPosition, OpenMatrix4f matrix) {
			this.put(entryPosition, matrix, OpenMatrix4f::mul);
		}
		
		public void put(String entryPosition, OpenMatrix4f matrix, MatrixOperation operation) {
			if (this.matrices.containsKey(entryPosition)) {
				Pair<OpenMatrix4f, MatrixOperation> entryValue = this.matrices.get(entryPosition);
				OpenMatrix4f result = entryValue.getSecond().mul(entryValue.getFirst(), matrix, null);
				this.matrices.put(entryPosition, Pair.of(result, operation));
			} else {
				this.matrices.put(entryPosition, Pair.of(new OpenMatrix4f(matrix), operation));
			}
		}
		
		public OpenMatrix4f getResult() {
			OpenMatrix4f result = new OpenMatrix4f();
			
			for (String entryName : BINDING_PRIORITY) {
				if (this.matrices.containsKey(entryName)) {
					Pair<OpenMatrix4f, MatrixOperation> pair = this.matrices.get(entryName);
					pair.getSecond().mul(result, pair.getFirst(), result);
				}
			}
			
			return result;
		}
	}
	
	private static final FloatBuffer MATRIX_TRANSFORMER = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	
	/*
	 * m00 m01 m02 m03
	 * m10 m11 m12 m13
	 * m20 m21 m22 m23
	 * m30 m31 m32 m33
	 */
	public float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33;
	
	public OpenMatrix4f() {
		this.setIdentity();
	}
	
	public OpenMatrix4f(final OpenMatrix4f src) {
		load(src);
	}
	
	public OpenMatrix4f setIdentity() {
		return setIdentity(this);
	}
	
	/**
	 * Set the given matrix to be the identity matrix.
	 * @param m The matrix to set to the identity
	 * @return m
	 */
	public static OpenMatrix4f setIdentity(OpenMatrix4f m) {
		m.m00 = 1.0f;
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;
		m.m10 = 0.0f;
		m.m11 = 1.0f;
		m.m12 = 0.0f;
		m.m13 = 0.0f;
		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = 1.0f;
		m.m23 = 0.0f;
		m.m30 = 0.0f;
		m.m31 = 0.0f;
		m.m32 = 0.0f;
		m.m33 = 1.0f;
		return m;
	}
	
	public OpenMatrix4f load(OpenMatrix4f src) {
		return load(src, this);
	}

	public static OpenMatrix4f load(OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m02 = src.m02;
		dest.m03 = src.m03;
		dest.m10 = src.m10;
		dest.m11 = src.m11;
		dest.m12 = src.m12;
		dest.m13 = src.m13;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m30 = src.m30;
		dest.m31 = src.m31;
		dest.m32 = src.m32;
		dest.m33 = src.m33;
		return dest;
	}
	
	public static OpenMatrix4f load(OpenMatrix4f mat, float[] elements) {
		if (mat == null) mat = new OpenMatrix4f();
		
		mat.m00 = elements[0];
		mat.m01 = elements[1];
		mat.m02 = elements[2];
		mat.m03 = elements[3];
		mat.m10 = elements[4];
		mat.m11 = elements[5];
		mat.m12 = elements[6];
		mat.m13 = elements[7];
		mat.m20 = elements[8];
		mat.m21 = elements[9];
		mat.m22 = elements[10];
		mat.m23 = elements[11];
		mat.m30 = elements[12];
		mat.m31 = elements[13];
		mat.m32 = elements[14];
		mat.m33 = elements[15];
		
		return mat;
	}
	
	public static OpenMatrix4f load(OpenMatrix4f mat, FloatBuffer buf) {
		if (mat == null) mat = new OpenMatrix4f();
		buf.position(0);
		mat.m00 = buf.get();
		mat.m01 = buf.get();
		mat.m02 = buf.get();
		mat.m03 = buf.get();
		mat.m10 = buf.get();
		mat.m11 = buf.get();
		mat.m12 = buf.get();
		mat.m13 = buf.get();
		mat.m20 = buf.get();
		mat.m21 = buf.get();
		mat.m22 = buf.get();
		mat.m23 = buf.get();
		mat.m30 = buf.get();
		mat.m31 = buf.get();
		mat.m32 = buf.get();
		mat.m33 = buf.get();
		
		return mat;
	}
	
	public OpenMatrix4f toFloat() {
		float[] elements = new float[16];
		
		elements[0] = m00;
		elements[1] = m01;
		elements[2] = m02;
		elements[3] = m03;
		elements[4] = m10;
		elements[5] = m11;
		elements[6] = m12;
		elements[7] = m13;
		elements[8] = m20;
		elements[9] = m21;
		elements[10] = m22;
		elements[11] = m23;
		elements[12] = m30;
		elements[13] = m31;
		elements[14] = m32;
		elements[15] = m33;
		
		return this;
	}
	
	public static OpenMatrix4f add(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}

		dest.m00 = left.m00 + right.m00;
		dest.m01 = left.m01 + right.m01;
		dest.m02 = left.m02 + right.m02;
		dest.m03 = left.m03 + right.m03;
		dest.m10 = left.m10 + right.m10;
		dest.m11 = left.m11 + right.m11;
		dest.m12 = left.m12 + right.m12;
		dest.m13 = left.m13 + right.m13;
		dest.m20 = left.m20 + right.m20;
		dest.m21 = left.m21 + right.m21;
		dest.m22 = left.m22 + right.m22;
		dest.m23 = left.m23 + right.m23;
		dest.m30 = left.m30 + right.m30;
		dest.m31 = left.m31 + right.m31;
		dest.m32 = left.m32 + right.m32;
		dest.m33 = left.m33 + right.m33;

		return dest;
	}
	
	public OpenMatrix4f mulFront(OpenMatrix4f mulTransform) {
		return OpenMatrix4f.mul(mulTransform, this, this);
	}
	
	public OpenMatrix4f mulBack(OpenMatrix4f mulTransform) {
		return OpenMatrix4f.mul(this, mulTransform, this);
	}
	
	public static OpenMatrix4f mul(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		float m00 = left.m00 * right.m00 + left.m10 * right.m01 + left.m20 * right.m02 + left.m30 * right.m03;
		float m01 = left.m01 * right.m00 + left.m11 * right.m01 + left.m21 * right.m02 + left.m31 * right.m03;
		float m02 = left.m02 * right.m00 + left.m12 * right.m01 + left.m22 * right.m02 + left.m32 * right.m03;
		float m03 = left.m03 * right.m00 + left.m13 * right.m01 + left.m23 * right.m02 + left.m33 * right.m03;
		float m10 = left.m00 * right.m10 + left.m10 * right.m11 + left.m20 * right.m12 + left.m30 * right.m13;
		float m11 = left.m01 * right.m10 + left.m11 * right.m11 + left.m21 * right.m12 + left.m31 * right.m13;
		float m12 = left.m02 * right.m10 + left.m12 * right.m11 + left.m22 * right.m12 + left.m32 * right.m13;
		float m13 = left.m03 * right.m10 + left.m13 * right.m11 + left.m23 * right.m12 + left.m33 * right.m13;
		float m20 = left.m00 * right.m20 + left.m10 * right.m21 + left.m20 * right.m22 + left.m30 * right.m23;
		float m21 = left.m01 * right.m20 + left.m11 * right.m21 + left.m21 * right.m22 + left.m31 * right.m23;
		float m22 = left.m02 * right.m20 + left.m12 * right.m21 + left.m22 * right.m22 + left.m32 * right.m23;
		float m23 = left.m03 * right.m20 + left.m13 * right.m21 + left.m23 * right.m22 + left.m33 * right.m23;
		float m30 = left.m00 * right.m30 + left.m10 * right.m31 + left.m20 * right.m32 + left.m30 * right.m33;
		float m31 = left.m01 * right.m30 + left.m11 * right.m31 + left.m21 * right.m32 + left.m31 * right.m33;
		float m32 = left.m02 * right.m30 + left.m12 * right.m31 + left.m22 * right.m32 + left.m32 * right.m33;
		float m33 = left.m03 * right.m30 + left.m13 * right.m31 + left.m23 * right.m32 + left.m33 * right.m33;
		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;
		return dest;
	}
	
	public static OpenMatrix4f mulAsOrigin(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		float x = right.m30;
		float y = right.m31;
		float z = right.m32;
		OpenMatrix4f result = mul(left, right, dest);
		result.m30 = x;
		result.m31 = y;
		result.m32 = z;
		return result;
	}
	
	public static OpenMatrix4f mulAsOriginFront(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		return mulAsOrigin(right, left, dest);
	}
	
	public static OpenMatrix4f overwriteRotation(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		
		dest.m00 = right.m00;
		dest.m10 = right.m10;
		dest.m20 = right.m20;
		dest.m01 = right.m01;
		dest.m11 = right.m11;
		dest.m21 = right.m21;
		dest.m02 = right.m02;
		dest.m12 = right.m12;
		dest.m22 = right.m22;
		dest.m30 = left.m30;
		dest.m31 = left.m31;
		dest.m32 = left.m32;
		
		return dest;
	}
	
	public static Vec4f transform(OpenMatrix4f matrix, Vec4f src, Vec4f dest) {
		if (dest == null) {
			dest = new Vec4f();
		}
		
		float x = matrix.m00 * src.x + matrix.m10 * src.y + matrix.m20 * src.z + matrix.m30 * src.w;
		float y = matrix.m01 * src.x + matrix.m11 * src.y + matrix.m21 * src.z + matrix.m31 * src.w;
		float z = matrix.m02 * src.x + matrix.m12 * src.y + matrix.m22 * src.z + matrix.m32 * src.w;
		float w = matrix.m03 * src.x + matrix.m13 * src.y + matrix.m23 * src.z + matrix.m33 * src.w;
		
		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;
		
		return dest;
	}
	
	public static Vec3 transform(OpenMatrix4f matrix, Vec3 src) {
		double x = matrix.m00 * src.x + matrix.m10 * src.y + matrix.m20 * src.z + matrix.m30 * 1.0F;
		double y = matrix.m01 * src.x + matrix.m11 * src.y + matrix.m21 * src.z + matrix.m31 * 1.0F;
		double z = matrix.m02 * src.x + matrix.m12 * src.y + matrix.m22 * src.z + matrix.m32 * 1.0F;
		
		return new Vec3(x, y ,z);
	}
	
	public static Vec3f transform3v(OpenMatrix4f matrix, Vec3f src, Vec3f dest) {
		if (dest == null) {
			dest = new Vec3f();
		}
		
		Vec4f result = transform(matrix, new Vec4f(src.x, src.y, src.z, 1.0F), null);
		dest.x = result.x;
		dest.y = result.y;
		dest.z = result.z;
		
		return dest;
	}
	
	public OpenMatrix4f transpose() {
		return transpose(this);
	}
	
	public OpenMatrix4f transpose(OpenMatrix4f dest) {
		return transpose(this, dest);
	}
	
	public static OpenMatrix4f transpose(OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
		   dest = new OpenMatrix4f();
		}
		float m00 = src.m00;
		float m01 = src.m10;
		float m02 = src.m20;
		float m03 = src.m30;
		float m10 = src.m01;
		float m11 = src.m11;
		float m12 = src.m21;
		float m13 = src.m31;
		float m20 = src.m02;
		float m21 = src.m12;
		float m22 = src.m22;
		float m23 = src.m32;
		float m30 = src.m03;
		float m31 = src.m13;
		float m32 = src.m23;
		float m33 = src.m33;
		
		dest.m00 = m00;
		dest.m01 = m01;
		dest.m02 = m02;
		dest.m03 = m03;
		dest.m10 = m10;
		dest.m11 = m11;
		dest.m12 = m12;
		dest.m13 = m13;
		dest.m20 = m20;
		dest.m21 = m21;
		dest.m22 = m22;
		dest.m23 = m23;
		dest.m30 = m30;
		dest.m31 = m31;
		dest.m32 = m32;
		dest.m33 = m33;
		
		return dest;
	}

	public float determinant() {
		float f = m00 * ((m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32) - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
		f -= m01 * ((m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32) - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
		f += m02 * ((m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31) - m13 * m21 * m30	- m10 * m23 * m31 - m11 * m20 * m33);
		f -= m03 * ((m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31) - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
		
		return f;
	}
	
	private static float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
		return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
	}
	
	public OpenMatrix4f invert() {
		return OpenMatrix4f.invert(this, this);
	}
	
	public static OpenMatrix4f invert(OpenMatrix4f src, OpenMatrix4f dest) {
		float determinant = src.determinant();
		if (determinant != 0) {
			if (dest == null) {
				dest = new OpenMatrix4f();
			}
			float determinant_inv = 1.0F / determinant;

			float t00 =  determinant3x3(src.m11, src.m12, src.m13, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t01 = -determinant3x3(src.m10, src.m12, src.m13, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t02 =  determinant3x3(src.m10, src.m11, src.m13, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t03 = -determinant3x3(src.m10, src.m11, src.m12, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			float t10 = -determinant3x3(src.m01, src.m02, src.m03, src.m21, src.m22, src.m23, src.m31, src.m32, src.m33);
			float t11 =  determinant3x3(src.m00, src.m02, src.m03, src.m20, src.m22, src.m23, src.m30, src.m32, src.m33);
			float t12 = -determinant3x3(src.m00, src.m01, src.m03, src.m20, src.m21, src.m23, src.m30, src.m31, src.m33);
			float t13 =  determinant3x3(src.m00, src.m01, src.m02, src.m20, src.m21, src.m22, src.m30, src.m31, src.m32);
			float t20 =  determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m31, src.m32, src.m33);
			float t21 = -determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m30, src.m32, src.m33);
			float t22 =  determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m30, src.m31, src.m33);
			float t23 = -determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m30, src.m31, src.m32);
			float t30 = -determinant3x3(src.m01, src.m02, src.m03, src.m11, src.m12, src.m13, src.m21, src.m22, src.m23);
			float t31 =  determinant3x3(src.m00, src.m02, src.m03, src.m10, src.m12, src.m13, src.m20, src.m22, src.m23);
			float t32 = -determinant3x3(src.m00, src.m01, src.m03, src.m10, src.m11, src.m13, src.m20, src.m21, src.m23);
			float t33 =  determinant3x3(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12, src.m20, src.m21, src.m22);

			dest.m00 = t00*determinant_inv;
			dest.m11 = t11*determinant_inv;
			dest.m22 = t22*determinant_inv;
			dest.m33 = t33*determinant_inv;
			dest.m01 = t10*determinant_inv;
			dest.m10 = t01*determinant_inv;
			dest.m20 = t02*determinant_inv;
			dest.m02 = t20*determinant_inv;
			dest.m12 = t21*determinant_inv;
			dest.m21 = t12*determinant_inv;
			dest.m03 = t30*determinant_inv;
			dest.m30 = t03*determinant_inv;
			dest.m13 = t31*determinant_inv;
			dest.m31 = t13*determinant_inv;
			dest.m32 = t23*determinant_inv;
			dest.m23 = t32*determinant_inv;
			return dest;
		} else {
			return null;
		}
	}
	
	public OpenMatrix4f translate(float x, float y, float z) {
		return translate(new Vec3f(x, y ,z), this);
	}
	
	public OpenMatrix4f translate(Vec3f vec) {
		return translate(vec, this);
	}
	
	public OpenMatrix4f translate(Vec3f vec, OpenMatrix4f dest) {
		return translate(vec, this, dest);
	}

	public static OpenMatrix4f translate(Vec3f vec, OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		
		dest.m30 += src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z;
		dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
		dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
		dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;
		return dest;
	}
	
	public static OpenMatrix4f createTranslation(float x, float y, float z) {
		return new OpenMatrix4f().translate(new Vec3f(x, y ,z));
	}
	
	public static OpenMatrix4f createScale(float x, float y, float z) {
		return new OpenMatrix4f().scale(new Vec3f(x, y ,z));
	}
	
	public OpenMatrix4f rotateDeg(float angle, Vec3f axis) {
		return rotate((float)Math.toRadians(angle), axis);
	}
	
	public OpenMatrix4f rotate(float angle, Vec3f axis) {
		return rotate(angle, axis, this);
	}
	
	public OpenMatrix4f rotate(float angle, Vec3f axis, OpenMatrix4f dest) {
		return rotate(angle, axis, this, dest);
	}
	
	public static OpenMatrix4f createRotatorDeg(float angle, Vec3f axis) {
		return rotate((float)Math.toRadians(angle), axis, new OpenMatrix4f(), null);
	}
	
	public static OpenMatrix4f rotate(float angle, Vec3f axis, OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x * axis.y;
		float yz = axis.y * axis.z;
		float xz = axis.x * axis.z;
		float xs = axis.x * s;
		float ys = axis.y * s;
		float zs = axis.z * s;

		float f00 = axis.x * axis.x * oneminusc+c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;
		// n[3] not used
		float f10 = xy * oneminusc - zs;
		float f11 = axis.y * axis.y * oneminusc+c;
		float f12 = yz * oneminusc + xs;
		// n[7] not used
		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z * axis.z * oneminusc+c;

		float t00 = src.m00 * f00 + src.m10 * f01 + src.m20 * f02;
		float t01 = src.m01 * f00 + src.m11 * f01 + src.m21 * f02;
		float t02 = src.m02 * f00 + src.m12 * f01 + src.m22 * f02;
		float t03 = src.m03 * f00 + src.m13 * f01 + src.m23 * f02;
		float t10 = src.m00 * f10 + src.m10 * f11 + src.m20 * f12;
		float t11 = src.m01 * f10 + src.m11 * f11 + src.m21 * f12;
		float t12 = src.m02 * f10 + src.m12 * f11 + src.m22 * f12;
		float t13 = src.m03 * f10 + src.m13 * f11 + src.m23 * f12;
		dest.m20 = src.m00 * f20 + src.m10 * f21 + src.m20 * f22;
		dest.m21 = src.m01 * f20 + src.m11 * f21 + src.m21 * f22;
		dest.m22 = src.m02 * f20 + src.m12 * f21 + src.m22 * f22;
		dest.m23 = src.m03 * f20 + src.m13 * f21 + src.m23 * f22;
		dest.m00 = t00;
		dest.m01 = t01;
		dest.m02 = t02;
		dest.m03 = t03;
		dest.m10 = t10;
		dest.m11 = t11;
		dest.m12 = t12;
		dest.m13 = t13;
		
		return dest;
	}
	
	public Vec3f toTranslationVector() {
		return toVector(this);
	}
	
	public static Vec3f toVector(OpenMatrix4f matrix) {
		return new Vec3f(matrix.m30, matrix.m31, matrix.m32);
	}
	
	public Quaternion toQuaternion() {
		return OpenMatrix4f.toQuaternion(this);
	}
	
	public static Quaternion toQuaternion(OpenMatrix4f matrix) {
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
	
	public static OpenMatrix4f fromQuaternion(Quaternion quaternion) {
		OpenMatrix4f matrix = new OpenMatrix4f();
		float x = quaternion.i();
		float y = quaternion.j();
		float z = quaternion.k();
		float w = quaternion.r();
		float xy = x * y;
		float xz = x * z;
		float xw = x * w;
		float yz = y * z;
		float yw = y * w;
		float zw = z * w;
		float xSquared = 2F * x * x;
		float ySquared = 2F * y * y;
		float zSquared = 2F * z * z;
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
	
	public OpenMatrix4f scale(float x, float y, float z) {
		return this.scale(new Vec3f(x, y, z));
	}
	
	public OpenMatrix4f scale(Vec3f vec) {
		return scale(vec, this, this);
	}
	
	public static OpenMatrix4f scale(Vec3f vec, OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		dest.m00 = src.m00 * vec.x;
		dest.m01 = src.m01 * vec.x;
		dest.m02 = src.m02 * vec.x;
		dest.m03 = src.m03 * vec.x;
		dest.m10 = src.m10 * vec.y;
		dest.m11 = src.m11 * vec.y;
		dest.m12 = src.m12 * vec.y;
		dest.m13 = src.m13 * vec.y;
		dest.m20 = src.m20 * vec.z;
		dest.m21 = src.m21 * vec.z;
		dest.m22 = src.m22 * vec.z;
		dest.m23 = src.m23 * vec.z;
		return dest;
	}
	
	public Vec3f toScaleVector() {
		return new Vec3f(new Vec3f(this.m00, this.m01, this.m02).length(), new Vec3f(this.m10, this.m11, this.m12).length(), new Vec3f(this.m20, this.m21, this.m22).length());
	}
	
	public OpenMatrix4f removeTranslation() {
		return removeTranslation(this);
	}
	
	public static OpenMatrix4f removeTranslation(OpenMatrix4f src) {
		OpenMatrix4f copy = new OpenMatrix4f(src);
		copy.m30 = 0.0F;
		copy.m31 = 0.0F;
		copy.m32 = 0.0F;
		
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append('\n');
		buf.append(m00).append(' ').append(m10).append(' ').append(m20).append(' ').append(m30).append('\n');
		buf.append(m01).append(' ').append(m11).append(' ').append(m21).append(' ').append(m31).append('\n');
		buf.append(m02).append(' ').append(m12).append(' ').append(m22).append(' ').append(m32).append('\n');
		buf.append(m03).append(' ').append(m13).append(' ').append(m23).append(' ').append(m33).append('\n');
		return buf.toString();
	}
	
	public static Matrix4f exportToMojangMatrix(OpenMatrix4f visibleMat) {
		float[] arr = new float[16];
		arr[0] = visibleMat.m00;
		arr[1] = visibleMat.m10;
		arr[2] = visibleMat.m20;
		arr[3] = visibleMat.m30;
		arr[4] = visibleMat.m01;
		arr[5] = visibleMat.m11;
		arr[6] = visibleMat.m21;
		arr[7] = visibleMat.m31;
		arr[8] = visibleMat.m02;
		arr[9] = visibleMat.m12;
		arr[10] = visibleMat.m22;
		arr[11] = visibleMat.m32;
		arr[12] = visibleMat.m03;
		arr[13] = visibleMat.m13;
		arr[14] = visibleMat.m23;
		arr[15] = visibleMat.m33;
		
		return new Matrix4f(arr);
	}
	
	public static OpenMatrix4f importFromMojangMatrix(Matrix4f mat4f) {
		MATRIX_TRANSFORMER.position(0);
		mat4f.store(MATRIX_TRANSFORMER);
		
		return OpenMatrix4f.load(null, MATRIX_TRANSFORMER);
	}
}