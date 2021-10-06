package yesman.epicfight.animation;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.math.vector.Quaternion;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.MultiplyFunction;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class JointTransform {
	public static final String DYNAMIC_TRANSFORM = "dynamic_rotation";
	public static final String PARENT = "parent";
	
	public static class TransformEntry {
		private String transformName;
		private MultiplyFunction multiplyFunction;
		private JointTransform transform;
		
		private TransformEntry(String transformName, MultiplyFunction multiplyFunction, JointTransform transform) {
			this.transformName = transformName;
			this.multiplyFunction = multiplyFunction;
			this.transform = transform;
		}
	}
	
	private List<TransformEntry> transformEntries = Lists.newArrayList();
	private Vec3f position;
	private Vec3f scale;
	private Quaternion rotation;

	public JointTransform(Vec3f position, Quaternion rotation, Vec3f scale) {
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public Vec3f getPosition() {
		return this.position;
	}

	public Quaternion getRotation() {
		return this.rotation;
	}
	
	public void set(JointTransform jt) {
		Vec3f newV = jt.getPosition();
		Quaternion newQ = jt.getRotation();
		Vec3f newS = jt.scale;
		this.getPosition().set(newV.x, newV.y, newV.z);
		this.getRotation().set(newQ.getX(), newQ.getY(), newQ.getZ(), newQ.getW());
		this.scale.set(newS.x, newS.y, newS.z);
		jt.transformEntries.forEach((transformEntry) -> {
			this.transformEntries.add(new TransformEntry(transformEntry.transformName, transformEntry.multiplyFunction, transformEntry.transform));
		});
	}
	
	public void push(String name, MultiplyFunction mulFucntion, JointTransform transform) {
		this.transformEntries.add(new TransformEntry(name, mulFucntion, transform));
	}
	
	public JointTransform getTransformEntry(String name) {
		for (TransformEntry entry : this.transformEntries) {
			if (entry.transformName.equals(name)) {
				return entry.transform;
			}
		}
		
		return null;
	}
	
	public JointTransform getTransformEntryOrElse(String name, JointTransform defaultValue) {
		JointTransform transformEntry = getTransformEntry(name);
		return transformEntry == null ? defaultValue : transformEntry;
	}
	
	public OpenMatrix4f toMatrixSimple() {
		OpenMatrix4f matrix = new OpenMatrix4f();
		matrix.translate(this.position);
		OpenMatrix4f.mul(matrix, OpenMatrix4f.fromQuaternion(this.rotation), matrix);
		matrix.scale(this.scale);
		return matrix;
	}
	
	public OpenMatrix4f toMatrix() {
		OpenMatrix4f matrix = new OpenMatrix4f();
		this.transformEntries.forEach((transformEntry) -> {
			matrix.load(transformEntry.transform.toMatrixSimple());
			matrix.push(transformEntry.transformName, transformEntry.multiplyFunction);
		});
		matrix.load(this.toMatrixSimple());
		return matrix;
	}
	
	@Override
	public String toString() {
		return String.format("%s %s entry number : %d", this.position, this.rotation, this.transformEntries.size());
	}
	
	public static JointTransform interpolateSimple(JointTransform prev, JointTransform next, float progression) {
		return new JointTransform(MathUtils.interpolateVector(prev.position, next.position, progression),
				MathUtils.interpolateQuaternion(prev.rotation, next.rotation, progression),
				MathUtils.interpolateVector(prev.scale, next.scale, progression));
	}
	
	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression) {
		if (prev == null || next == null) {
			return JointTransform.empty();
		}
		JointTransform interpolated = interpolateSimple(prev, next, progression);
		prev.transformEntries.forEach((entry) -> {
			JointTransform transform = next.getTransformEntryOrElse(entry.transformName, JointTransform.empty());
			interpolated.push(entry.transformName, entry.multiplyFunction, interpolateSimple(entry.transform, transform, progression));
		});
		next.transformEntries.forEach((entry) -> {
			if (interpolated.getTransformEntry(entry.transformName) == null) {
				interpolated.push(entry.transformName, entry.multiplyFunction, interpolateSimple(JointTransform.empty(), entry.transform, progression));
			}
		});
		return interpolated;
	}
	
	public static JointTransform fromMatrix(OpenMatrix4f matrix) {
		return new JointTransform(new Vec3f(matrix.m30, matrix.m31, matrix.m32), OpenMatrix4f.toQuaternion(matrix), new Vec3f(1.0F, 1.0F, 1.0F));
	}
	
	public static JointTransform of(Vec3f vec) {
		return new JointTransform(vec, new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vec3f(1.0F, 1.0F, 1.0F));
	}
	
	public static JointTransform of(Quaternion quat) {
		return new JointTransform(new Vec3f(0.0F, 0.0F, 0.0F), quat, new Vec3f(1.0F, 1.0F, 1.0F));
	}
	
	public static JointTransform of(Vec3f vec, Quaternion quat) {
		return new JointTransform(vec, quat, new Vec3f(1.0F, 1.0F, 1.0F));
	}
	
	public static JointTransform empty() {
		return new JointTransform(new Vec3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vec3f(1.0F, 1.0F, 1.0F));
	}
}