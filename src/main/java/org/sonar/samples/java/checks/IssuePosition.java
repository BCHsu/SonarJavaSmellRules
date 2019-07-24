package org.sonar.samples.java.checks;

import org.smell.astmodeler.Node;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.issue.NewIssue;

public class IssuePosition {
	private NewIssue brokenModularizationIssue;

	private InputFile javaFile;
	private Node node;
	
	public NewIssue getBrokenModularizationIssue() {
		return brokenModularizationIssue;
	}

	public void setBrokenModularizationIssue(NewIssue brokenModularizationIssue) {
		this.brokenModularizationIssue = brokenModularizationIssue;
	}

	public InputFile getJavaFile() {
		return javaFile;
	}

	public void setJavaFile(InputFile javaFile) {
		this.javaFile = javaFile;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	
	public IssuePosition(NewIssue brokenModularizationIssue, InputFile javaFile, Node node){
		this.brokenModularizationIssue= brokenModularizationIssue;
		this.javaFile= javaFile;
		this.node= node;
	}
}
