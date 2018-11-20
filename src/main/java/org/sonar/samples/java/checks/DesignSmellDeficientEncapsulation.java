package org.sonar.samples.java.checks;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;

@Rule(key = "S120")

public class DesignSmellDeficientEncapsulation extends BaseTreeVisitor implements JavaFileScanner {

	private JavaFileScannerContext context;
	private static List<ClassNode> classes = new ArrayList<ClassNode>();
	//Not test yet
	private Smell smell = new DeficientEncapsulation();
	public static int filecounts = 0;
	private int fileCount = 0;
	
	// 逐一走訪專案中的所有classes
	// 先對一個file中的每個class各呼叫一次visitClass 接著對這個file呼叫一次scanFile

	// 第一次訪問某個class的時候先把class放入這個準備進行分析的list(classes)中
	// 在分析每個file的時候檢查 classes中存放的每個ClassNode是否有smell

	// 一直需要檢查取出來的東西是否為null 想一個比較general的解決方法來取代 一直用if(XXx!=null)做check
	@Override
	public void visitClass(ClassTree classTree) {
		ClassNode classNode = new ClassNode(classTree);
	
		int classComplexity = context.getComplexityNodes(classTree).size();
		classNode.setWMC(classComplexity);
		classes.add(classNode);

		super.visitClass(classTree);
	}

	public static List<ClassNode> getClasses() {
		return classes;
	}

	// 每掃描一個檔案 就會執行一次scanFile方法 因此把偵測smell的部分移到這裡
	@Override
	public void scanFile(JavaFileScannerContext context) {
		this.context = context;
		CompilationUnitTree cut = context.getTree();
	
		scan(cut);
		
		//偵測的邏輯要放在scan()後面才能正確地讀取到最後一個檔案
		
		for (ClassNode classNode : classes) {
			
			if (classNode.haveSmell(smell)  ) {				
				//TODO reportBrokenModularization
			}
		}	
	}

	public static void logOnFile(String filePath, String issueName) throws ClassNotFoundException {
		String path = filePath;
		File f = new File(path);
		if (!f.exists()) {
			try {
				FileWriter fw = null;
				f.createNewFile();
				fw = new FileWriter(f, true);
				String log = issueName;
				fw.write(log);
				if (fw != null) {
					fw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
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
}