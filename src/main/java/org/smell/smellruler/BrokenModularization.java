package org.smell.smellruler;

public class BrokenModularization implements Smell {


	@Override
	public boolean is(Type type) {
		Smell.Type smellType = type();
		return smellType == type;
	}

	@Override
	public Type type() {
		return Smell.Type.BROKENMODULARIATION;
	}

	@Override
	public String smellDetail() {
		return "Broken Modularization Location!";
	}
}