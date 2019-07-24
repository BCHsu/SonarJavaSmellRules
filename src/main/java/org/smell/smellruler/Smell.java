package org.smell.smellruler;

public interface Smell {
	boolean is(Smell.Type type);
	public Smell.Type type();
	
	public String smellDetail();
	enum Type {
		 DATACLASS,
		 FEATUREENVY,
		 BROKENMODULARIATION;
	}
}