package org.sonar.samples.java.checks;

import org.sonar.plugins.java.api.tree.Tree;

public interface MyNodeVisitor {
	Tree tree = null;
	
	String getData() throws ClassNotFoundException;
	
}