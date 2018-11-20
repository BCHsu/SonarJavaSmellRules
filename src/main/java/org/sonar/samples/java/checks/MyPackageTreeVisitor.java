package org.sonar.samples.java.checks;

import java.util.Iterator;

import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.PackageDeclarationTree;
import org.sonar.plugins.java.api.tree.Tree;

public class MyPackageTreeVisitor implements MyNodeVisitor {

	PackageDeclarationTree packageDeclarationTree;


	public MyPackageTreeVisitor(PackageDeclarationTree packageDeclarationTree) {
		this.packageDeclarationTree = packageDeclarationTree;
	}
	@Override
	public String getData() throws ClassNotFoundException {
		if (this.packageDeclarationTree.is(Tree.Kind.PACKAGE)) {
			return getPackageBodyData(this.packageDeclarationTree);
		}
		return getPackageBodyData(null);
	}

	private String getPackageBodyData(PackageDeclarationTree packageDeclarationTree) {
		if (packageDeclarationTree.is(Tree.Kind.PACKAGE)) {
			String writeData = "";
			
			ExpressionTree expressionTree =  packageDeclarationTree.packageName();
			
			IdentifierTree idTree = ((MemberSelectExpressionTree) expressionTree).identifier();
			writeData = writeData + " IdentifierTree : "+ idTree+ "\r\n";	
			
			
			ExpressionTree epTree = ((MemberSelectExpressionTree) expressionTree).expression();
			writeData = writeData + " ExpressionTree : "+ epTree+ "\r\n";	

			return writeData;
		}
		return "error";
	}
}