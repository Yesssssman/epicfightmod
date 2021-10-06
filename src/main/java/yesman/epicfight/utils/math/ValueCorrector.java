package yesman.epicfight.utils.math;

public class ValueCorrector {
	private float adderArg;
	private float multiplierArgs;
	private float setterArgs;
	
	public ValueCorrector(float adder, float multiplier, float setter) {
		this.adderArg = adder;
		this.multiplierArgs = multiplier;
		this.setterArgs = setter;
	}
	
	public void merge(ValueCorrector valueCorrector) {
		this.adderArg += valueCorrector.adderArg;
		this.multiplierArgs += valueCorrector.multiplierArgs;
		this.setterArgs += valueCorrector.setterArgs;
	}
	
	public float get(float value) {
		return this.setterArgs == 0 ? (value * (1.0F + this.multiplierArgs)) + this.adderArg : this.setterArgs;
	}
	
	@Override
	public String toString() {
		return this.setterArgs == 0
				? String.format("%.0f%%", (1.0F + this.multiplierArgs) * 100.0F) + (this.adderArg == 0 ? "" : String.format(" + %.1f", this.adderArg))
				: String.format("%.0f", this.setterArgs);
	}
	
	public static ValueCorrector base() {
		return new ValueCorrector(0, 0, 0);
	}
	
	public static ValueCorrector getMultiplier(float arg) {
		return new ValueCorrector(0, arg, 0);
	}
	
	public static ValueCorrector getSetter(float arg) {
		return new ValueCorrector(0, 0, arg);
	}
	
	public static ValueCorrector getAdder(float arg) {
		return new ValueCorrector(arg, 0, 0);
	}
}
