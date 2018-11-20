package org.sonar.samples.java.checks;

import java.util.List;

import org.sonar.plugins.java.api.tree.Tree;

public class DataClass implements Smell {

	private Metric woc;
	private Metric wmc;
	private Metric nopaAndNoam;
	private static String logBrokenModularization = "D:\\test\\BrokenModularization.txt";
	
	public DataClass() {
		initializeMetrics();
		
	}
	
	private void initializeMetrics() {
		this.woc= new Woc();
		this.wmc= new Wmc();
		this.nopaAndNoam= new NopaAndNoam();
	}
	
	@Override
	public boolean detected(Node node) {
		if(haveDataClassSmell(node)) {
			return true;
		}
		return false;
	}
	
	
	private boolean haveDataClassSmell(Node node) {
		configureMetrics(node);
		
		//if (wocLessThanThreshold() && ((accessorAndFieldsGreaterThanFew() && wmcLessThanHigh()) || (accessorAndFieldsGreaterThanMany() && wmcLessThanVeryHigh()))) {
		if ( ((Woc) woc).lessThanThreshold() && 
			  ( (((NopaAndNoam)nopaAndNoam).greaterThanFew()  && ((Wmc) wmc).lessThanSuperSuperHigh()) ||
			    (((NopaAndNoam)nopaAndNoam).greaterThanMany() && ((Wmc) wmc).lessThanSuperSuperHigh()) 
			  )
		   ){
			try {
				String info = "wocLessThanThreshold : " + ((ClassNode)node).getName() + "\r\n";
				info = info +"woc : "+  ((Woc)woc).getValue() + "\r\n";
				info = info +"wmc : "+  ((Wmc)wmc).getValue()+ "\r\n";
				info = info +"NOPA : "+  ((NopaAndNoam)nopaAndNoam).getNopaValue() + "\r\n";
				info = info +"NOAM : "+  ((NopaAndNoam)nopaAndNoam).getNoamValue() + "\r\n";
				
				DesignSmellDeficientEncapsulation.logOnFile(logBrokenModularization, info);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
	

	private void configureMetrics(Node node) {
		ClassNode classNode = ((ClassNode)node);
		float woc = classNode.configureWeightOfClass();
		((Woc)this.woc).setValue(woc);
		
		int wmc = classNode.getWMC();
		((Wmc)this.wmc).setValue(wmc);
		//TODO 
		//Other metrics
		List<Tree> publicAttributes = classNode.getPublicVariables();
		int nopa = publicAttributes.size();
		((NopaAndNoam)this.nopaAndNoam).setNopaValue(nopa);

		List<Tree> getterAndSetterMethods = classNode.getGetterAndSetterMethods();
		int noam = getterAndSetterMethods.size();
		((NopaAndNoam)this.nopaAndNoam).setNoamValue(noam);
	}

/*
	private boolean accessorAndFieldsGreaterThanFew() {
		if (this.classNode.getNOPA() + this.classNode.getNOAM() > ACCESSOR_OR_FIELD_FEW_LEVEL) {
			return true;
		}
		return false;
	}

	private boolean accessorAndFieldsGreaterThanMany() {
		if (this.classNode.getNOPA() + this.classNode.getNOAM() > ACCESSOR_OR_FIELD_MANY_LEVEL) {
			return true;
		}
		return false;
	}
*/

}
