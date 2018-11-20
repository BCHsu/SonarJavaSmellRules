package org.sonar.samples.java.checks;

public class NopaAndNoam implements Metric {
	private static final int ACCESSOR_OR_FIELD_FEW_LEVEL = 3;
	private static final int ACCESSOR_OR_FIELD_MANY_LEVEL = 5;
	
	private int nopaValue;
	
	private int noamValue;

	public void setNopaValue(int nopa) {
		this.nopaValue =nopa;
	}
	
	public void setNoamValue(int noam) {
		this.noamValue =noam;
	}
	
	public int getNoamValue() {
		return this.noamValue;
	}
	
	public int getNopaValue() {
		return this.nopaValue;
	}
	
	public boolean greaterThanFew() {
		return (this.nopaValue + this.noamValue) > ACCESSOR_OR_FIELD_FEW_LEVEL;
	}
	
	public boolean greaterThanMany() {
		return (this.nopaValue + this.noamValue) > ACCESSOR_OR_FIELD_MANY_LEVEL;
	}
}
