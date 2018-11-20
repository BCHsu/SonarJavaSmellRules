package org.sonar.samples.java.checks;

public class DeficientEncapsulation implements Smell {
	private Smell dataClass;
	private Smell featureEnvy;

	
	public DeficientEncapsulation() {
		dataClass = new DataClass();
		featureEnvy = new FeatureEnvy();
	}
	
	@Override
	public boolean detected(Node node) {
		return haveDeficientEncapsulation(node);		
	}
	
	private boolean haveDeficientEncapsulation(Node node) {
		if(this.dataClass.detected(node) | this.featureEnvy.detected(node)) {
			return true;
		}
		return false;
	}

}
