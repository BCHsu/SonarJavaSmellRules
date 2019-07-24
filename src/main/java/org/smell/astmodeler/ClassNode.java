//ATFD先不用考慮package 和 class間的關係 
//與專案中的其他類別做比對
//屬於專案中的其他類別但是不屬於method owner的就視為外部類別

package org.smell.astmodeler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.smell.smellruler.Smell;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

public class ClassNode extends SmellableNode{
	private ClassTree classTree;
	private String className;
	private List<MethodNode> methods;	
	private int wmc;
	private static final Node.Kind kind = Node.Kind.CLASS;

	public ClassNode(ClassTree classTree) {
		initializeClassModel(classTree);
		initializeMethodModel();
	}
	
	private void initializeClassModel(ClassTree classTree){
		smellLists = new ArrayList<Smell>();
		this.classTree = classTree;
		this.className = "" + this.classTree.simpleName();
		setStartLine(classTree.openBraceToken().line());
	}
	

	public List<MethodNode> getAllMethodNodes() {
		return this.methods;
	}

	@Override
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

	private void initializeMethodModel() {	
		this.methods = new ArrayList<MethodNode>();
		List<Tree> allMethods = getAllMethods();
		for (Tree m : allMethods) {
			if (isMethod(m)) {
				MethodNode methodNode = new MethodNode((MethodTree) m);		
				//connect classnode and methodnode
				methodNode.setOwner(this);
				methods.add(methodNode);								
			}
		}
	}

	
	private boolean isMethod(Tree tree){
		return tree.is(Tree.Kind.METHOD);
	}
	
	private boolean isVariable(Tree tree){
		return tree.is(Tree.Kind.VARIABLE);
	}
	
	private boolean isPublic(Tree member){
		if(isMethod(member)){
			return ((MethodTree) member).symbol().isPublic();
		}
		if(isVariable(member)){
			return ((VariableTree) member).symbol().isPublic();
		}
		
		return false;
	}
	
	private Stream<Tree> getClassTreeMemebersStream(){
		return this.classTree.members().stream();
	}
	public List<Tree> getPublicMethods() {
		// 抓取classTree中的所有member 再透過filter過濾出public methods

		return getClassTreeMemebersStream()			
				// 先找出Tree.Kind.METHOD 再過濾中其中修飾元是public的方法
				// member.is(A,B) 可以同時過濾出A跟B兩種節點
				// 也可以串接多個filter增加篩選條件
		.filter(member -> isPublicMethod(member))
				.collect(Collectors.toList());
	}
	
	private List<Tree> getAllMethods() {
		return getClassTreeMemebersStream()
				.filter(member -> isMethod(member))
				.collect(Collectors.toList());
	}
	
	private boolean isPublicVariable(Tree member){
		return isVariable(member)  && isPublic(member);
	}
	
	private boolean isPublicMethod(Tree member){
		return isMethod(member)  && isPublic(member);
	}
	
	public List<Tree> getPublicVariables() {
		return  getClassTreeMemebersStream()		
				.filter(member -> isPublicVariable(member) )
				.collect(Collectors.toList());
	}

	public List<Tree> getPublicMembers() {
		return  getClassTreeMemebersStream()
						.filter(member -> ( isPublicVariable(member)) 
						| isPublicMethod(member))
						.collect(Collectors.toList());
	}

	private boolean isGetterMethod(Tree member){
		String getterRegex = "^get.+";	
		return isPublicMethod(member) && methodNameisStartWith(member,getterRegex);
	}
	
	
	private boolean isSetterMethod(Tree member){
		String setterRegex = "^set.+";
		return isPublicMethod(member) && methodNameisStartWith(member,setterRegex);
	}
	
	private boolean methodNameisStartWith(Tree member, String targetString){
		if(isMethod(member)){
			return ((MethodTree) member).simpleName().toString().matches(targetString);
		}
		return false;
	}	
	
	public List<Tree> getGetterAndSetterMethods() {
		// 找出public的getter跟setter方法
		// 判斷getter/setter方法的依據: 方法名稱開頭為getXXX或setXXX
		return  getClassTreeMemebersStream()
						.filter(member -> isGetterMethod(member) | 
								isSetterMethod(member))
						.collect(Collectors.toList());
	}
	
	@Override
	public Node.Kind kind() {
		return kind;
	}
}