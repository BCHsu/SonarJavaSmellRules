package org.sonar.samples.java.checks;

public class Atfd implements Metric {
	private int value;
	
	private static final int FEW_ATFD_THRESHOLD = 5;
	

	public void setValue(int metricsValue) {
		this.value = metricsValue;

	}

	public int getValue() {
		return this.value;
	}
	
	public boolean atfdGreaterThanFew() {
		
		return this.value > FEW_ATFD_THRESHOLD;
	}
	
	public boolean atfdAlwaysTrue() {
	
		return true;
	}
}
