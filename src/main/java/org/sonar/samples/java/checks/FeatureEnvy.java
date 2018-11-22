package org.sonar.samples.java.checks;

import java.util.List;

import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

//需要取出  if()  for() while() 判斷式內的statements

public class FeatureEnvy implements Smell {

	private Metric atfd;
	private static String atfdDetected = "D:\\test\\atfdDetected.txt";
	private static String atfdValue = "D:\\test\\atfdForEachClass.txt";
	private static String atfdResult = "D:\\test\\atfdSmell.txt";
	public FeatureEnvy() {
		// this.name = "DataClass";
		initializeMetrics();
	}

	private void initializeMetrics() {
		this.atfd = new Atfd();
	}

	@Override
	public boolean detected(Node node) {
		return haveFeatureEnvySmell(node);
	}

	private boolean haveFeatureEnvySmell(Node node) {
		// logMetrics(node);
		((Atfd) this.atfd).setValue(calculateMetrics(node));
		String log="";
		log = log 
				+ "Class Name : " + node.getName()+"\r\n"
				+ "Atfd : "+  ((Atfd) this.atfd).getValue() +"\r\n";								
				
		try {
			DesignSmellDeficientEncapsulation.logOnFile(atfdValue, log);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if (((Atfd) atfd).atfdGreaterThanFew()) {
			return true;
		}
		
		//TODO test
		if (((Atfd) atfd).atfdGreaterThan(1)) {
			String information="ATFD detected in " + node.getName() +"\r\n" ;
			try {
				DesignSmellDeficientEncapsulation.logOnFile(atfdResult, information);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	private int calculateMetrics(Node node) {
		ClassNode classNode = (ClassNode) node;
		int atfdCounts = 0;
		List<MethodNode> methods = classNode.getAllMethodNodes();
		for (MethodNode n : methods) {	
			atfdCounts = atfdCounts + calculateATFD(n);
		}
		return atfdCounts;
	}

	private int calculateATFD(Node node) {
		MethodNode methNode = (MethodNode) node;
		int atfd = 0;
		//呼叫每一個methodNode計算它們的ATFD
		List<StatementTree> statements = methNode.getStatementsInMethodNode();
		for(StatementTree s : statements) {
			atfd = atfd + analysisStatementsATFD(s);
		}
		return atfd;
	}

	
	private int analysisStatementsATFD(StatementTree s) {

		//TODO
		if (s.is(Tree.Kind.VARIABLE)) {
			// IdentifierTree idTree = ((VariableTree) s).simpleName();
			return 0;
		}else if (s.is(Tree.Kind.EXPRESSION_STATEMENT)) {
			ExpressionTree epTree = ((ExpressionStatementTree) s).expression();
			if (epTree.is(Tree.Kind.METHOD_INVOCATION)) {
				
				String log = "";
				//List<ClassNode> classes = DesignSmellDeficientEncapsulation.getClasses();
				ExpressionTree methodSelect = ((MethodInvocationTree) epTree).methodSelect() ;
				
				
				//如果ExpressionTree中的type是自己定義的類別的話 取出來就都會是!unknown! 
				//其他公用API則可以取出對應的type
				
				//可能是需要提供正確的binary檔才能取出對應的ExpressionTree
				if(methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
					ExpressionTree ep = ((MemberSelectExpressionTree)methodSelect).expression();
					IdentifierTree id = ((MemberSelectExpressionTree)methodSelect).identifier() ;
					if (ep!= null && id!=null) {
						if (isGetterMethod(id.name())) {							
							
							/*
							if(ep.is(Tree.Kind.IDENTIFIER)) {
								
							}*/
							/*
							for (ClassNode cn : classes) {
								if (methodOwner.equals(cn.getName())) {
									// information = information + " Found Method owner equals Symbol owner" +
									// "\r\n";
								}
							}	*/
							
							//if(id.symbol().toString().equals("!unknownSymbol!")) {
								log = log //+"Symbol name: " + methodSymbol.name() + "\r\n"
										+ "expression : " + ep +"\r\n"
										+ "expression type: " + ep.symbolType() +"\r\n"									
										+ "identifier name : "+ id.name() +"\r\n";		
								log = log + "identifier symbol: "+ id.symbol() +"\r\n"	
										  + "identifier symbol name: "+ id.symbol().name() +"\r\n"
										  + "identifier symbol type: "+ id.symbol().type() +"\r\n";
								try {
									DesignSmellDeficientEncapsulation.logOnFile(atfdDetected, log);
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
								return 1;
							//}
							
						}										
					}
				}							
			}
		}
		return 0;
	}
	
	private boolean isGetterMethod(String methodName) {
		String getterRegex = "^get.+";
		return methodName.matches(getterRegex);
	}
}
