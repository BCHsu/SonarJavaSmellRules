//ATFD需要考慮到 if else switch while for裡面的內容
//if else tree底下也有List<StatementTree> 所以是composite pattern 可以嘗試用遞迴去取出ATFD

//ATFD再加上考慮到 variableTree(Tree.Kind.VARIABLE)的情況 
//把Decor的實驗案例 apply到現有的dataclass rule上 

//更新UML圖

package org.sonar.samples.java.checks;

import java.util.Arrays;
import java.util.List;

import org.sonar.java.ast.visitors.ComplexityVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.Lists;

public class MethodNode implements Node {
	private MethodTree methodTree;
	private String methodName;

	private BlockTree blockTree;

	public MethodNode(MethodTree methodTree) {
		this.methodTree = methodTree;
		this.methodName = this.methodTree.simpleName().name();
		this.blockTree = this.methodTree.block();
	}

	public String getMethodName() {
		return this.methodName;
	}

	public BlockTree getBlockTree() {
		return this.blockTree;
	}
}
