package yesman.epicfight.utils.math;

@FunctionalInterface
public interface MultiplyFunction {
	public OpenMatrix4f mul(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest);
}