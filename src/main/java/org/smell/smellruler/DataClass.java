package org.smell.smellruler;

public class DataClass implements Smell {
	private float  wocValue;
	private int  wmcValue;
	private int nopaAndNoamValue;
	private static final Smell.Type smellType = Smell.Type.DATACLASS;
	
	public DataClass(float woc, int wmc, int nopaAndNoam) {
		this.wocValue = woc;
		this.wmcValue = wmc;
		this.nopaAndNoamValue = nopaAndNoam;
	}

	@Override
	public Smell.Type type() {
		return smellType;
	}
	
	@Override
	public boolean is(Smell.Type type) {
		return smellType == type;
	}
	
	public float getWocValue() {
		return wocValue;
	}

	public void setWocValue(float woc) {
		this.wocValue = woc;
	}

	public int getWmcValue() {
		return wmcValue;
	}

	public void setWmcValue(int wmc) {
		this.wmcValue = wmc;
	}

	public int getNopaAndNoamValue() {
		return nopaAndNoamValue;
	}

	public void setNopaAndNoamValue(int nopaAndNoam) {
		this.nopaAndNoamValue = nopaAndNoam;
	}

	@Override
	public String smellDetail() {
		String dataClassMessage = "Data Class detected" + "\r\n" + "WOC: " + this.getWocValue() + "\r\n" + "WMC: "
				+ this.getWmcValue() + "\r\n" + "NOPA + NOAM: " + this.getNopaAndNoamValue() + "\r\n";
		return dataClassMessage;
	}
}