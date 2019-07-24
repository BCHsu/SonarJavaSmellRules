package org.sonar.samples.java.checks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.DCDetector;
import org.smell.astmodeler.FEDetector;
import org.smell.astmodeler.MethodNode;
import org.smell.astmodeler.Node;
import org.smell.astmodeler.Smellable;
import org.smell.rule.pluginregister.JavaRulesDefinition;
import org.smell.smellruler.DataClass;
import org.smell.smellruler.FeatureEnvy;
import org.smell.smellruler.Smell;
import org.smell.smellruler.Smell.Type;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;

//TODO
//Not implement yet:
//method body以外的ATFD
//方法參數中的ATFD ex: setValue(B.getValue);
//return statement中的內容
// +-*% 等含有運算元的內容

//記錄所有method的LAA
//偵測api的FE新增為另外一條rule
//過濾掉測試檔案

//內部類別中有FE的時候 reportIssue的位置應該要在內部類別上

@Rule(key = "DM001")

public class BrokenModularizationRule extends BaseTreeVisitor implements Sensor, JavaFileScanner {

	private JavaFileScannerContext context;
	private static List<ClassNode> classes = new ArrayList<ClassNode>();
	private FileSystem fs;

	// 逐一走訪專案中的所有classes
	// 先對一個file中的每個class各呼叫一次visitClass 接著對這個file呼叫一次scanFile

	// 第一次訪問某個class的時候先把class放入這個準備進行分析的list(classes)中
	// 在分析每個file的時候檢查 classes中存放的每個ClassNode是否有smell

	// 一直需要檢查取出來的東西是否為null 想一個比較general的解決方法來取代 一直用if(XXx!=null)做check
	@Override
	public void visitClass(ClassTree classTree) {
		initializeModels(classTree);
		super.visitClass(classTree);
	}

	private void initializeModels(ClassTree classTree) {
		ClassNode classNode = new ClassNode(classTree);
		File file = context.getFile();
		int classComplexity = context.getComplexityNodes(classTree).size();
		classNode.setWMC(classComplexity);
		classNode.setFile(file);
		classes.add(classNode);
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Compute number of files");
		descriptor.onlyOnLanguage("java");
		descriptor.createIssuesForRuleRepositories(JavaRulesDefinition.REPOSITORY_KEY);
	}

	// execute方法會在scanFile方法執行完後才執行
	@Override
	public void execute(SensorContext context) {
		detectBMSmell();
		fs = context.fileSystem();
		for (ClassNode classNode : classes) {
			reportSmellIssues(context, classNode);
		}
	}

	private int getNodeStartLine(Smellable smellableNode) {
		return smellableNode.getStartLine();
	}

	private Smell generateSmellInstance(Smellable node, Smell.Type smellType) {
		if (smellType == Smell.Type.DATACLASS || smellType == Smell.Type.FEATUREENVY) {
			return node.getRegisteredSmell(smellType);
		}
		return null;
	}

	private String generateIssueDetail(Smell smell) {
		if (smell.is(Smell.Type.DATACLASS)) {
			DataClass dc = (DataClass) smell;
			return dc.smellDetail();
		}
		if (smell.is(Smell.Type.FEATUREENVY)) {
			FeatureEnvy fe = (FeatureEnvy) smell;
			return fe.smellDetail();
		}
		return null;
	}

	private void reportSmell(IssuePosition issuePosition, Smell.Type type) {
		ClassNode classNode = (ClassNode) issuePosition.getNode();
		switch(type){
			case DATACLASS:
				if(checkNodeHaveDataClassSmell(classNode) && generateSmellInstance(classNode, type) != null){
					DataClass dc = (DataClass) generateSmellInstance(classNode, type);
					String dataClassMessage = generateIssueDetail(dc);
					// locate issue position
					locateIssuePosition(issuePosition, dataClassMessage);
				}
				break;
			case FEATUREENVY:
				List<MethodNode> methods = classNode.getAllMethodNodes();
				for (MethodNode methodNode : methods) {
					// if have FE物件 report BM detected and FE detected
					if (checkNodeHaveFeatureEnvySmell(methodNode) && generateSmellInstance(classNode, type) != null) {
						FeatureEnvy fe = (FeatureEnvy) generateSmellInstance(classNode, type);
						String featureEnvyMessage = generateIssueDetail(fe);
						locateIssuePosition(issuePosition, featureEnvyMessage);
					}
				}
				break;
		default:
			break;				
		}
	}
	
	private void reportSmellIssues(SensorContext context, ClassNode classNode) {
		if (checkNodeHaveBMSmell(classNode)) {
			if (getFiletoReport(classNode) != null) {
				Iterable<InputFile> javaFiletoReprotBMIssue = getFiletoReport(classNode);
				for (InputFile javaFile : javaFiletoReprotBMIssue) {
					NewIssue brokenModularizationIssue = context.newIssue().forRule(JavaRulesDefinition.RULE_ON_LINE_1)
							// gap is used to estimate the remediation cost to
							// fix the debt
							.gap(2.0);

					// generate issue message and locate issue position
					NewIssueLocation brokenModularizationLocation = brokenModularizationIssue.newLocation().on(javaFile)
							.at(javaFile.selectLine(getNodeStartLine(classNode)))
							.message("Broken Modularization Location!");
					brokenModularizationIssue.at(brokenModularizationLocation);
					IssuePosition issuePosition = new IssuePosition(brokenModularizationIssue, javaFile, classNode);
					
					reportSmell(issuePosition, Smell.Type.DATACLASS);
					reportSmell(issuePosition, Smell.Type.FEATUREENVY);
					brokenModularizationIssue.save();
				}
			}
		}
	}

	// locate issue position
	private void locateIssuePosition(IssuePosition issuePosition, String issueMessage) {
		NewIssue brokenModularizationIssue = issuePosition.getBrokenModularizationIssue();
		InputFile javaFile = issuePosition.getJavaFile();
		Node node = issuePosition.getNode();
		if (node.is(Node.Kind.CLASS) || node.is(Node.Kind.METHOD)) {
			Smellable smellableNode = (Smellable) node;
			NewIssueLocation issueLocation = brokenModularizationIssue.newLocation().on(javaFile)
					.at(javaFile.selectLine(getNodeStartLine(smellableNode))).message(issueMessage);
			brokenModularizationIssue.addLocation(issueLocation);
		}
	}

	private boolean checkNodeHaveFeatureEnvySmell(MethodNode methodNode) {
		return methodNode != null && methodNode.haveSmell(Smell.Type.FEATUREENVY);
	}

	private boolean checkNodeHaveBMSmell(ClassNode classNode) {
		return classNode != null && classNode.haveSmell(Smell.Type.BROKENMODULARIATION);
	}

	private boolean checkNodeHaveDataClassSmell(ClassNode classNode) {
		return classNode != null && classNode.haveSmell(Smell.Type.DATACLASS);
	}

	private void detectBMSmell() {
		// 遞迴檢查每個classnode 並計算DC的值
		for (ClassNode classNode : classes) {
			detectSmells(classNode);
			List<MethodNode> methods = classNode.getAllMethodNodes();
			// 遞迴檢查每個classnode裡的methodnode 並計算FE的值
			for (MethodNode method : methods) {
				detectSmells(method);
			}
		}
	}
	
	private void caculateDCSmell(ClassNode classNode, Type smell){
		if(smell==Smell.Type.DATACLASS){
			//detector 接收classNode來偵測classNode的smell metrics
			//if classNode DC metrics 滿足閥值
			// create DC物件並加入classNode的metric lists	
			DCDetector dcDetector = new DCDetector();
			dcDetector.detect(classNode);							
		}
	}
	
	private void caculateFESmell(MethodNode methodNode, Type smell) {			
		if(smell==Smell.Type.FEATUREENVY){
			FEDetector feDetector = new FEDetector();
			feDetector.detect(methodNode);			
		} 
	}

	private void detectSmells(Smellable node) {
		Node smellableNode = (Node) node;
		if (smellableNode.is(Node.Kind.CLASS)) {			
			ClassNode classNode = (ClassNode) node;			
			caculateDCSmell(classNode,Smell.Type.DATACLASS);
		}
		if (smellableNode.is(Node.Kind.METHOD)) {
			MethodNode methodNode = (MethodNode) node;
			caculateFESmell(methodNode,Smell.Type.FEATUREENVY);
		}
	}

	private Iterable<InputFile> getFiletoReport(Node node) {
		if (node.is(Node.Kind.CLASS)) {
			ClassNode classNode = (ClassNode) node;
			return getFileofClass(classNode);
		}
		if (node.is(Node.Kind.METHOD)) {
			MethodNode methodNode = (MethodNode) node;
			ClassNode classNode = methodNode.getOwner();
			return getFileofClass(classNode);
		}
		return null;
	}

	private Iterable<InputFile> getFileofClass(ClassNode classNode) {
		// // InputFile是相對於sonar.sources 的檔案路徑 Ex:
		// // src\chess\ChessBoard.java
		// // File則是絕對路徑 Ex: C:\Users\\user\eclipse-workspace\Expert
		// // System\src\chess\\ChessBoard.java
		String javaFilePath = classNode.getFile().getPath();
		return (Iterable<InputFile>) fs.inputFiles(fs.predicates().hasPath(javaFilePath));
	}

	public static List<ClassNode> getClasses() {
		return classes;
	}

	// 每掃描一個檔案 就會執行一次scanFile方法
	@Override
	public void scanFile(JavaFileScannerContext context) {
		this.context = context;
		CompilationUnitTree cut = context.getTree();
		// 如果沒有scan就不能正常運作
		// scan => accept BaseTreeVisitor => visitor visitClass(this);
		scan(cut);
		// // 取出檔案的type Ex: class enum...
		// List<Tree> trees = cut.types();
		// for (Tree tree : trees) {
		// // TODO
		// // maybe enum 跟interface沒有被考慮到
		// if (tree.is(Tree.Kind.CLASS)) {
		// // addClassNodes((ClassTree) tree);
		//
		// // TODO 不使用visitClass 就會漏計算到smell
		// ClassNode classNode = new ClassNode((ClassTree) tree);
		// // log = log + tree.kind() + "\r\n";
		// log = log + classNode.getName() + "\r\n";
		// int classComplexity = context.getComplexityNodes((ClassTree)
		// tree).size();
		// classNode.setWMC(classComplexity);
		// File file = context.getFile();
		// classNode.setFile(file);
		// classes.add(classNode);
	}

	public static void logOnFile(String filePath, String issueName) throws ClassNotFoundException {
		String path = filePath;
		File f = new File(path);
		if (!f.exists()) {
			try {
				f.createNewFile();
				writeToFile(f, issueName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			writeToFile(f, issueName);
		}
	}

	private static void writeToFile(File f, String issueName) {
		try {
			FileWriter fw = null;
			fw = new FileWriter(f, true);
			String log = issueName;
			fw.write(log);
			if (fw != null) {
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}