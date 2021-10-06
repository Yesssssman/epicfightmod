package yesman.epicfight.utils.math;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;

import com.google.common.collect.Lists;

import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;

public class OpenMatrix4f {
	public static class MatrixEntry {
		private String matrixName;
		private MultiplyFunction multiplyFunction;
		private OpenMatrix4f matrix;
		
		private MatrixEntry(String matrixName, MultiplyFunction multiplyFunction, OpenMatrix4f matrix) {
			this.matrixName = matrixName;
			this.multiplyFunction = multiplyFunction;
			this.matrix = matrix;
		}
		
		public OpenMatrix4f getMatrix() {
			return this.matrix;
		}
		
		public static MatrixEntry searchUtil(String name) {
			return new MatrixEntry(name, null, null);
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof MatrixEntry) {
				return ((MatrixEntry)obj).matrixName.equals(this.matrixName);
			}
			return super.equals(obj);
		}
	}
	
	private List<MatrixEntry> multiplyEntries = Lists.newArrayList();
	
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
	
	public void push(String matrixName) {
		this.push(matrixName, OpenMatrix4f::mul);
	}
	
	public void push(String matrixName, MultiplyFunction mulFunction) {
		this.multiplyEntries.add(new MatrixEntry(matrixName, mulFunction, new OpenMatrix4f(this)));
		this.setIdentity();
	}
	
	public void addMatrix(int index, String matrixName, OpenMatrix4f matrix4f) {
		this.addMatrix(index, matrixName, OpenMatrix4f::mul, matrix4f);
	}
	
	public void addMatrix(int index, String matrixName, MultiplyFunction mulFunction, OpenMatrix4f matrix4f) {
		this.multiplyEntries.add(index, new MatrixEntry(matrixName, mulFunction, new OpenMatrix4f(matrix4f)));
	}
	
	public static OpenMatrix4f pushOrMul(String name, String newname, OpenMatrix4f entryLeft, OpenMatrix4f entryRight, OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		
		int index = dest.multiplyEntries.indexOf(MatrixEntry.searchUtil(name));
		
		if (index >= 0) {
			if (entryLeft != null) {
				dest.addMatrix(index, newname, entryLeft);
			}
			if (entryRight != null) {
				dest.addMatrix(index + 1, newname, entryRight);
			}
		} else {
			if (left != null) {
				OpenMatrix4f.mul(left, dest, dest);
			}
			if (right != null) {
				OpenMatrix4f.mul(dest, right, dest);
			}
		}
		
		return dest;
	}
	
	public OpenMatrix4f getResult() {
		MultiplyFunction mulFunction = OpenMatrix4f::mul;
		OpenMatrix4f result = new OpenMatrix4f();
		for (MatrixEntry matrixEntry : this.multiplyEntries) {
			mulFunction.mul(result, matrixEntry.matrix, result);
			mulFunction = matrixEntry.multiplyFunction;
		}
		mulFunction.mul(result, this, result);
		return result;
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
	
	public OpenMatrix4f load(FloatBuffer buf) {
		return OpenMatrix4f.load(this, buf);
	}
	
	public OpenMatrix4f store(FloatBuffer buf) {
		buf.put(m00);
		buf.put(m01);
		buf.put(m02);
		buf.put(m03);
		buf.put(m10);
		buf.put(m11);
		buf.put(m12);
		buf.put(m13);
		buf.put(m20);
		buf.put(m21);
		buf.put(m22);
		buf.put(m23);
		buf.put(m30);
		buf.put(m31);
		buf.put(m32);
		buf.put(m33);
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
	
	public static OpenMatrix4f mulOnOrigin(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest) {
		float x = right.m30;
		float y = right.m31;
		float z = right.m32;
		OpenMatrix4f result = mul(left, right, dest);
		result.m30 = x;
		result.m31 = y;
		result.m32 = z;
		
		return result;
	}
	
	public static Vec4f transform(OpenMatrix4f left, Vec4f right, Vec4f dest) {
		if (dest == null) {
			dest = new Vec4f();
		}
		
		float x = left.m00 * right.x + left.m10 * right.y + left.m20 * right.z + left.m30 * right.w;
		float y = left.m01 * right.x + left.m11 * right.y + left.m21 * right.z + left.m31 * right.w;
		float z = left.m02 * right.x + left.m12 * right.y + left.m22 * right.z + left.m32 * right.w;
		float w = left.m03 * right.x + left.m13 * right.y + left.m23 * right.z + left.m33 * right.w;
		
		dest.x = x;
		dest.y = y;
		dest.z = z;
		dest.w = w;
		
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
	
	public static OpenMatrix4f invert(OpenMatrix4f src, OpenMatrix4f dest) {
		float determinant = src.determinant();
		if (determinant != 0) {
			if (dest == null)
				dest = new OpenMatrix4f();
			float determinant_inv = 1f/determinant;

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
		} else
			return null;
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
	
	public OpenMatrix4f rotate(float angle, Vec3f axis) {
		return rotate(angle, axis, this);
	}
	
	public OpenMatrix4f rotate(float angle, Vec3f axis, OpenMatrix4f dest) {
		return rotate(angle, axis, this, dest);
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
		float x = quaternion.getX();
		float y = quaternion.getY();
		float z = quaternion.getZ();
		float w = quaternion.getW();
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
	
	public static OpenMatrix4f rotate(float angle, Vec3f axis, OpenMatrix4f src, OpenMatrix4f dest) {
		if (dest == null) {
			dest = new OpenMatrix4f();
		}
		float c = (float) Math.cos(angle);
		float s = (float) Math.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x*axis.y;
		float yz = axis.y*axis.z;
		float xz = axis.x*axis.z;
		float xs = axis.x*s;
		float ys = axis.y*s;
		float zs = axis.z*s;

		float f00 = axis.x*axis.x*oneminusc+c;
		float f01 = xy*oneminusc+zs;
		float f02 = xz*oneminusc-ys;
		// n[3] not used
		float f10 = xy*oneminusc-zs;
		float f11 = axis.y*axis.y*oneminusc+c;
		float f12 = yz*oneminusc+xs;
		// n[7] not used
		float f20 = xz*oneminusc+ys;
		float f21 = yz*oneminusc-xs;
		float f22 = axis.z*axis.z*oneminusc+c;

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
	
	public OpenMatrix4f scale(Vec3f vec) {
		return scale(vec, this);
	}
	
	public OpenMatrix4f scale(Vec3f vec, OpenMatrix4f dest) {
		return scale(vec, this, dest);
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
	
	public static Matrix4f exportMatrix(OpenMatrix4f visibleMat) {
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
	
	public static OpenMatrix4f importMatrix(Matrix4f mat4f) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		buf.position(0);
		mat4f.write(buf);
		return OpenMatrix4f.load(null, buf);
	}
}