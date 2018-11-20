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

public class FeatureEnvy implements Smell {

	private Metric atfd;
	private static String logMemberStatement = "D:\\test\\getmemberStatement.txt";
	private static String logStatement = "D:\\test\\getStatement.txt";

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

		if (((Atfd) atfd).atfdGreaterThanFew()) {
			return true;
		}

		return false;
	}

	private int calculateMetrics(Node node) {
		ClassNode classNode = (ClassNode) node;
		int atfdCounts = 0;
		List<MethodNode> methods = classNode.getAllMethodNodes();
		for (MethodNode n : methods) {
			// TODO
			atfdCounts = atfdCounts + calculateATFD(n);
		}

		// TODO
		return atfdCounts + 100;

	}

	// TODO
	private int calculateATFD(Node node) {
		MethodNode methNode = (MethodNode) node;
		BlockTree blockTree = methNode.getBlockTree();
		List<StatementTree> methodStatements = getBlockStatements(blockTree);
		String information = "";
		for (StatementTree s : methodStatements) {
			
			//2018/11/20 node改成methNode TODO
			visitStatementTree(methNode, s, information);
		}
		// TODO
		if (1 > 0) {

		}

		return 1;
	}

	private boolean isSelectionStatements(StatementTree statement) {
		return statement.is(Tree.Kind.IF_STATEMENT) | statement.is(Tree.Kind.DO_STATEMENT) | statement.is(Tree.Kind.WHILE_STATEMENT) | statement.is(Tree.Kind.FOR_STATEMENT) | statement.is(Tree.Kind.SWITCH_STATEMENT);
	}

	private void visitSelectionStatement(Node node, StatementTree s, String logInformation) {
		if (s.is(Tree.Kind.IF_STATEMENT)) {
			StatementTree elseStatement = ((IfStatementTree) s).elseStatement();

			// elseStatement 可能會是某種ifStatement (else if)
			if (elseStatement != null) {
				if (elseStatement.is(Tree.Kind.IF_STATEMENT)) {
					visitSelectionStatement(node, elseStatement, logInformation);
				} else {
					getStatements(node, elseStatement, logInformation);
				}
			}

			StatementTree thenStatement = ((IfStatementTree) s).thenStatement();
			getStatements(node, thenStatement, logInformation);
		} else if (s.is(Tree.Kind.DO_STATEMENT)) {
			StatementTree doWhileStatement = ((DoWhileStatementTree) s).statement();
			getStatements(node, doWhileStatement, logInformation);
		} else if (s.is(Tree.Kind.WHILE_STATEMENT)) {
			StatementTree whileStatement = ((WhileStatementTree) s).statement();
			getStatements(node, whileStatement, logInformation);
		} else if (s.is(Tree.Kind.FOR_STATEMENT)) {
			StatementTree forStatement = ((ForStatementTree) s).statement();
			getStatements(node, forStatement, logInformation);
		} else if (s.is(Tree.Kind.SWITCH_STATEMENT)) {
			StatementTree switchStatement = s;
			getStatements(node, switchStatement, logInformation);
		} else if (s.is(Tree.Kind.FOR_EACH_STATEMENT)) {
			StatementTree forEachStatement = ((ForEachStatement) s).statement();
			getStatements(node, forEachStatement, logInformation);
		}
	}

	private void getStatements(Node node, StatementTree s, String logInformation) {
		if (s != null) {
			if (s.is(Tree.Kind.SWITCH_STATEMENT)) {
				List<CaseGroupTree> cases = ((SwitchStatementTree) s).cases();
				if (cases != null) {
					for (CaseGroupTree c : cases) {
						List<StatementTree> caseBlock = c.body();
						logBlockInformation(node, caseBlock, logInformation);
					}
				}
			} else if (!s.is(Tree.Kind.RETURN_STATEMENT)) {
				List<StatementTree> block = getBlockStatements((BlockTree) s);
				logBlockInformation(node, block, logInformation);
			}
		}
	}

	private void logBlockInformation(Node node, List<StatementTree> block, String logInformation) {
		if (block != null) {
			for (StatementTree st : block) {
				String log = logInformation;
				visitStatementTree(node, st, log);
			}
		}
	}

	String visitStatementTree(Node node, StatementTree s, String information) {
		String log = information;
		if (s.is(Tree.Kind.EXPRESSION_STATEMENT) || s.is(Tree.Kind.VARIABLE)) {
			visitStatement(node, s, log);
		}
		// 遞迴走訪 if else for switch等等區塊
		if (isSelectionStatements(s)) {
			visitSelectionStatement(node, s, log);
		}
		return log;
	}

	private String visitStatement(Node node, StatementTree s, String information) {

		if (s.is(Tree.Kind.VARIABLE)) {
			// IdentifierTree idTree = ((VariableTree) s).simpleName();
		}
		
		
		if (s.is(Tree.Kind.EXPRESSION_STATEMENT)) {
			ExpressionTree epTree = ((ExpressionStatementTree) s).expression();
			
			if (epTree.is(Tree.Kind.METHOD_INVOCATION)) {
				String log = "";
				Symbol methodSymbol = ((MethodInvocationTree) epTree).symbol();
				String methodOwner = methodSymbol.owner().name();
				List<ClassNode> classes = DesignSmellDeficientEncapsulation.getClasses();
				ExpressionTree methodSelect = ((MethodInvocationTree) epTree).methodSelect() ;
				
				if(methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
					ExpressionTree ep = ((MemberSelectExpressionTree)methodSelect).expression();
					IdentifierTree id = ((MemberSelectExpressionTree) methodSelect).identifier() ;
					//String info ="";
					if (ep!= null && id!=null) {
						if (id.name().equals("testabc")) {
							log = log //+"Symbol name: " + methodSymbol.name() + "\r\n"
									+ "expression : " + ep +"\r\n"
									+ "identifier : "+ id +"\r\n"
									+ "method name : "+ ((MethodNode) node).getMethodName() +"\r\n"
									+ "identifier name : "+ id.name() +"\r\n";
							
							try {
								DesignSmellDeficientEncapsulation.logOnFile(logStatement, log);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}										
					}
				}
				if (methodSymbol.name()!= null) {
				//String method = methodSelect.toString();
						if (methodSymbol.name().equals("testabc")) {
							log = log //+"Symbol name: " + methodSymbol.name() + "\r\n"
							+ "method owner name: " + methodSymbol.owner().name() + "\r\n"
							+ "package name: " + methodSymbol.owner().owner().name()  + "\r\n"
							+ "method select: " + methodSelect + "\r\n" 
							+ "method name: " + ((MethodNode) node).getMethodName() + "\r\n";
							
							try {
								DesignSmellDeficientEncapsulation.logOnFile(logStatement, log);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
					
				}
				for (ClassNode cn : classes) {
					// information = information +" ClassNodes :"+ cn.getName()+ "\r\n";
					if (methodOwner.equals(cn.getName())) {
						// information = information + " Found Method owner equals Symbol owner" +
						// "\r\n";
					}
				}
			}			
		}	
		return information;
	}

	private List<StatementTree> getBlockStatements(BlockTree blockTree) {
		if (blockTree.body() != null) {
			return blockTree.body();
		}
		return null;
	}
}
