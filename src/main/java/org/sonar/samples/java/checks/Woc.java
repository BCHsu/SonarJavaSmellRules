package org.sonar.samples.java.checks;

public class Woc implements Metric {
	private static final float WOC_THRESHOLD = (float) 1 / 3;
	private float value;
	
	public boolean lessThanThreshold() {
		return(this.value < WOC_THRESHOLD);
	}
	
	public void setValue(float value) {
		this.value=value;
	}

	public float getValue() {
		return this.value;
	}
}
