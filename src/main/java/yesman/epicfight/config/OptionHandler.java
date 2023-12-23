package yesman.epicfight.config;

public class OptionHandler<T> {
	protected final T defaultOption;
	protected T option;
	
	public OptionHandler(T defaultOption) {
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
	
	public static class IntegerOptionHandler extends OptionHandler<Integer> {
		private final int minValue;
		private final int maxValue;
		
		public IntegerOptionHandler(Integer defaultOption, int minValue, int maxValue) {
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
	
	public static class DoubleOptionHandler extends OptionHandler<Double> {
		private final double minValue;
		private final double maxValue;
		
		public DoubleOptionHandler(Double defaultOption, double minValue, double maxValue) {
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