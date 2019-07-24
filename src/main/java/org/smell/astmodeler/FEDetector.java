package org.smell.astmodeler;

import org.smell.metricruler.Atfd;
import org.smell.metricruler.Fdp;
import org.smell.metricruler.Laa;
import org.smell.metricruler.Metric;
import org.smell.smellruler.BrokenModularization;
import org.smell.smellruler.FeatureEnvy;

public class FEDetector implements Detector{
	private Metric laa;
	private Metric atfd;
	private Metric fdp;

	public FEDetector() {
		initializeMetrics();
	}

	private void initializeMetrics() {
		this.atfd = new Atfd();
		this.laa = new Laa();
		this.fdp = new Fdp();
	}

	private void calculateATFDMetric(Node node, MethodNode methodNode) {
		((Atfd) this.atfd).calculateMetric(node, methodNode);
	}

	@Override
	public void detect(Node node) {
		
		if(node.is(Node.Kind.METHOD)){
			// clear metrics for every methods
			// 每個class node應該要有各自的smell value
			MethodNode methodNode = (MethodNode)node;
			initializeMetrics();
			ClassNode classNode = methodNode.getOwner();
			calculateATFDMetric(classNode, methodNode);

			// laa是method level的metrics這邊挑出最低的laa來做為class的代表
			((Laa) this.laa).calculateMetric(classNode, methodNode);
			((Fdp) this.fdp).calculateMetric(classNode, methodNode);

			if (haveFeatureEnvy()) {
				float laaValue = ((Laa)laa).getMetricValue();
				int atfdValue = ((Atfd)atfd).getMetricValue();
				int fdpValue = ((Fdp)fdp).getMetricValue();
				
				// 為將metric資訊儲存在fe物件中
				FeatureEnvy fe = new FeatureEnvy(laaValue,atfdValue,fdpValue);
				BrokenModularization bm  = new BrokenModularization();
				classNode.registerSmell(bm);
				methodNode.registerSmell(fe);
			}		
		}	
	}
	
	private boolean haveFeatureEnvy(){
		return ((Atfd) atfd).greaterThanFew() && ((Laa) laa).lessThanThreshold() && ((Fdp) fdp).lessThanFew();
	}
}