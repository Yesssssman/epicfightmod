package yesman.epicfight.config;

public class Option<T> {
	protected final T defaultOption;
	protected T option;
	
	public Option(T defaultOption) {
		this.defaultOption = defaultOption;
		this.option = defaultOption;
	}
	
	public T getValue() {
		return this.option;
	}
	
	public void setValue(T option) {
		this.option = option;
	}
	
	public void setDefaultValue() {
		this.option = this.defaultOption;
	}
	
	public static class IntegerOption extends Option<Integer> {
		private final int minValue;
		private final int maxValue;
		
		public IntegerOption(Integer defaultOption, int minValue, int maxValue) {
			super(defaultOption);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public void setValue(Integer option) {
			if (option > this.maxValue) {
				this.option = this.minValue;
			} else if (option < this.minValue) {
				this.option = this.maxValue;
			} else {
				this.option = option;
			}
		}
	}
	
	public static class DoubleOption extends Option<Double> {
		private final double minValue;
		private final double maxValue;
		
		public DoubleOption(Double defaultOption, double minValue, double maxValue) {
			super(defaultOption);
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		@Override
		public void setValue(Double option) {
			if (option > this.maxValue) {
				this.option = this.minValue;
			} else if (option < this.minValue) {
				this.option = this.maxValue;
			} else {
				this.option = option;
			}
		}
	}
}