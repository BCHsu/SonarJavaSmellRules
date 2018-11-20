//ATFD先不用考慮package 和 class間的關係 
//改用與專案中的其他類別做比對
//不是method owner的就視為外部類別

package org.sonar.samples.java.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sonar.java.ast.visitors.ComplexityVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

public class ClassNode implements Node {
	private ClassTree classTree;
	private String className;
	private List<MethodNode> methods;
		
	private int wmc;

	public ClassNode(ClassTree classTree) {
		this.classTree = classTree;
		this.className = "" + this.classTree.simpleName();
		initializeMethodList();
	}

	public List<MethodNode> getAllMethodNodes() {
		return this.methods;
	}

	public String getName() {
		return this.className;
	}

	public ClassTree getClassTree() {
		return this.classTree;
	}
	
	public void setWMC(int wmc) {
		this.wmc = wmc;
	}

	public int getWMC() {
		return this.wmc;
	}

	private void initializeMethodList() {
		this.methods = new ArrayList<MethodNode>();
		List<Tree> allMethods = getAllMethods();

		for (Tree m : allMethods) {
			if (m.is(Tree.Kind.METHOD)) {
				MethodNode methodNode = new MethodNode((MethodTree) m);
				methods.add(methodNode);
			}
		}
	}

	public boolean haveSmell(Smell smell) {
		return smell.detected(this);
	}
	
	float configureWeightOfClass() {
		int publicMethodsCounts = getPublicMethods().size();
		int publicMembersCounts = getPublicMembers().size();
		float weightOfClass = (float) publicMethodsCounts / publicMembersCounts;
		return weightOfClass;
	}
	
	

	private List<Tree> getPublicMethods() {
		// 抓取classTree中的所有member 再透過filter過濾出public methods
		List<Tree> publicMethods = this.classTree.members().stream()
				// 先找出Tree.Kind.METHOD 再過濾中其中修飾元是public的方法
				// member.is(A,B) 可以同時過濾出A跟B兩種節點
				// 也可以串接多個filter增加篩選條件
				.filter(member -> member.is(Tree.Kind.METHOD) && ((MethodTree) member).symbol().isPublic()).collect(Collectors.toList());
		return publicMethods;
	}

	private List<Tree> getAllMethods() {
		// 抓取classTree中的所有member 再透過filter過濾出public methods
		List<Tree> allMethods = this.classTree.members().stream()
				// 先找出Tree.Kind.METHOD 再過濾中其中修飾元是public的方法
				// member.is(A,B) 可以同時過濾出A跟B兩種節點
				// 也可以串接多個filter增加篩選條件
				.filter(member -> member.is(Tree.Kind.METHOD)).collect(Collectors.toList());
		return allMethods;
	}

	List<Tree> getPublicVariables() {
		// 抓取classTree中的所有member 再透過filter過濾出public variables

		List<Tree> publicVariables = this.classTree.members().stream().filter(member -> member.is(Tree.Kind.VARIABLE) && ((VariableTree) member).symbol().isPublic()).collect(Collectors.toList());
		return publicVariables;
	}

	private List<Tree> getPublicMembers() {
		// 抓取classTree中的所有member 再透過filter過濾出public members

		// filter甚麼東西出來 去做分類 分類完畫整個專案的類別圖 包括配置檔
		List<Tree> pubicMembers = this.classTree.members().stream().filter(member -> ((member.is(Tree.Kind.VARIABLE) && ((VariableTree) member).symbol().isPublic())) | ((member.is(Tree.Kind.METHOD) && ((MethodTree) member).symbol().isPublic()))).collect(Collectors.toList());
		return pubicMembers;
	}

	List<Tree> getGetterAndSetterMethods() {
		String getterRegex = "^get.+";
		String setterRegex = "^set.+";

		// 找出public的getter跟setter方法
		// 判斷getter/setter方法的依據: 方法名稱開頭為getXXX或setXXX
		List<Tree> getterAndSetterMethods = this.classTree.members().stream().filter(member -> ((member.is(Tree.Kind.METHOD) && ((MethodTree) member).symbol().isPublic() && ((MethodTree) member).simpleName().toString().matches(getterRegex))) | ((member.is(Tree.Kind.METHOD) && ((MethodTree) member).symbol().isPublic() && ((MethodTree) member).simpleName().toString().matches(setterRegex)))).collect(Collectors.toList());
		return getterAndSetterMethods;
	}
}
