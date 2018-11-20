package org.sonar.samples.java.checks;

public class Wmc implements Metric {
	private int value;
	private static final int WMC_HIGH_LEVEL = 31;
	private static final int WMC_VERY_HIGH_LEVEL = 47;
	
	public boolean lessThanHigh() {
		return(this.value < WMC_HIGH_LEVEL);
	}

	public boolean lessThanSuperSuperHigh() {
		return(this.value < 1000);
	}
	
	public boolean lessThanVeryHigh() {
		return(this.value < WMC_VERY_HIGH_LEVEL);
	}
	
	public void setValue(int value) {
		this.value = value;

	}

	public int getValue() {
		return this.value;
	}

}
