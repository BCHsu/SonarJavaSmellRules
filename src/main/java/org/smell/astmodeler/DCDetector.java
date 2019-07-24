package org.smell.astmodeler;

import org.smell.metricruler.Metric;
import org.smell.metricruler.NopaAndNoam;
import org.smell.metricruler.Wmc;
import org.smell.metricruler.Woc;
import org.smell.smellruler.BrokenModularization;
import org.smell.smellruler.DataClass;
import org.sonar.samples.java.checks.BrokenModularizationRule;

public class DCDetector implements Detector{
	private Metric woc;
	private Metric wmc;
	private Metric nopaAndNoam;
	
	private static String logDataClass = "D:\\test\\DataClassSmell.txt";
	
	public DCDetector() {
		initializeMetrics();
	}

	private void initializeMetrics() {
		this.woc = new Woc();
		this.wmc = new Wmc();
		this.nopaAndNoam = new NopaAndNoam();
	}
	
	
	private boolean haveDataClass(){
		return ((Woc) woc).lessThanThreshold()
				&& ((((NopaAndNoam) nopaAndNoam).greaterThanFew() && ((Wmc) wmc).lessThanSuperSuperHigh())
						|| (((NopaAndNoam) nopaAndNoam).greaterThanMany() && ((Wmc) wmc).lessThanSuperSuperHigh()));
	}
	
	@Override
	public void detect(Node node) {
		calculateMetrics(node);
		if (haveDataClass()) {
			
			
			float wocValue = ((Woc) woc).getValue();
			int wmcValue = ((Wmc) wmc).getValue();
			int nopaValue = ((NopaAndNoam) nopaAndNoam).getNopaValue();
			int noamValue = ((NopaAndNoam) nopaAndNoam).getNoamValue();
			int nopaAndNoamValue = nopaValue + noamValue;
			
			try {
				String info = "wocLessThanThreshold : " + ((ClassNode) node).getName() + "\r\n";
				info = info + "woc : " + wocValue + "\r\n";
				info = info + "wmc : " + wmcValue + "\r\n";
				info = info + "NOPA : " + nopaValue + "\r\n";
				info = info + "NOAM : " + noamValue + "\r\n";
				info = info + " File owner : " + ((ClassNode) node).getFile() + "\r\n";
				BrokenModularizationRule.logOnFile(logDataClass, info);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// 改為將metric資訊儲存在DC物件中
			// ((ClassNode)node).setDataClass(this);

			//create DC 物件
			DataClass dc = new DataClass(wocValue,wmcValue,nopaAndNoamValue);
			BrokenModularization bm  = new BrokenModularization();
			((ClassNode)node).registerSmell(bm);
			((ClassNode)node).registerSmell(dc);
			
		} 	
	}
	
	private void calculateMetrics(Node node) {
		((Woc) this.woc).calculateMetric(node);
		((Wmc) this.wmc).calculateMetric(node);
		((NopaAndNoam) this.nopaAndNoam).calculateMetric(node);
	}
}
