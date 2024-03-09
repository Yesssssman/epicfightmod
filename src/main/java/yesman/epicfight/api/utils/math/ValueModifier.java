package yesman.epicfight.api.utils.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ValueModifier {
	public static final Codec<ValueModifier> CODECS = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.FLOAT.fieldOf("adder").forGetter(ValueModifier::getAdder),
			Codec.FLOAT.fieldOf("multiplier").forGetter(ValueModifier::getMultiplier),
			Codec.FLOAT.fieldOf("setter").forGetter(ValueModifier::getSetter)
		).apply(instance, ValueModifier::new)
	);
	
	private float adder;
	private float multiplier;
	private float setter = Float.NaN;
	
	public ValueModifier(float adder, float multiplier, float setter) {
		this.adder = adder;
		this.multiplier = multiplier;
		this.setter = setter;
	}
	
	public void merge(ValueModifier valueCorrector) {
		this.adder += valueCorrector.adder;
		this.multiplier *= valueCorrector.multiplier;
		this.setter = valueCorrector.setter;
	}
	
	public void eraseSetter() {
		this.setter = Float.NaN;
	}
	
	public float getAdder() {
		return this.adder;
	}
	
	public float getMultiplier() {
		return this.multiplier;
	}
	
	public float getSetter() {
		return this.setter;
	}
	
	public float getTotalValue(float value) {
		return this.setter == Float.NaN ? (value * this.multiplier) + this.adder : this.setter;
	}
	
	@Override
	public String toString() {
		return this.setter == Float.NaN
				? String.format("%.0f%%", this.multiplier * 100.0F) + (this.adder == 0 ? "" : String.format(" + %.1f", this.adder))
				: String.format("%.0f", this.setter);
	}
	
	public static ValueModifier empty() {
		return new ValueModifier(0.0F, 1.0F, Float.NaN);
	}
	
	public static ValueModifier adder(float arg) {
		return new ValueModifier(arg, 1.0F, Float.NaN);
	}
	
	public static ValueModifier multiplier(float arg) {
		return new ValueModifier(0.0F, arg, Float.NaN);
	}
	
	public static ValueModifier setter(float arg) {
		return new ValueModifier(0.0F, 1.0F, arg);
	}
}