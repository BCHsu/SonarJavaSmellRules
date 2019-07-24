package org.smell.smellruler;

public class FeatureEnvy implements Smell {
	
	private float  laaValue;
	private int  atfdValue;
	private int fdpValue;
	private static final Smell.Type smellType = Smell.Type.FEATUREENVY;
	
	public FeatureEnvy(float laa, int atfd, int fdp) {
		this.laaValue = laa;
		this.atfdValue = atfd;
		this.fdpValue = fdp;
	}

	public float getLaaValue() {
		return laaValue;
	}

	public void setLaaValue(float laaValue) {
		this.laaValue = laaValue;
	}

	public int getAtfdValue() {
		return atfdValue;
	}

	public void setAtfdValue(int atfdValue) {
		this.atfdValue = atfdValue;
	}

	public int getFdpValue() {
		return fdpValue;
	}

	public void setFdpValue(int fdpValue) {
		this.fdpValue = fdpValue;
	}

	@Override
	public Smell.Type type() {
		return smellType;
	}
	
	@Override
	public boolean is(Smell.Type type) {
		return smellType == type;
	}

	@Override
	public String smellDetail() {
		String featureEnvyMessage = "Feature Envy detected" + "\r\n" + "ATFD: " + this.getAtfdValue() + "\r\n" + "LAA: "
				+ this.getLaaValue() + "\r\n" + "FDP: " + this.getFdpValue() + "\r\n";
		return featureEnvyMessage;
	}
}