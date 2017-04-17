package com.linxcool.andlipse.action.cls;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IWorkbenchWindow;

import com.linxcool.andlipse.action.LJob;
import com.linxcool.andlipse.core.LConsole;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

public class ConstructorJob extends LJob {

	public ConstructorJob(IWorkbenchWindow window) {
		super("ConstructorJob", window);
	}
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return super.run(monitor);
	}

	@Override
	protected boolean execute(IProgressMonitor monitor) throws Exception {
		LConsole.print("start insert code to class...");

		File proDir = project.getLocation().toFile();
		File rulesFile = new File(proDir, "hotfix-rules.pro");
		if (!rulesFile.exists() || rulesFile.isDirectory()) {
			LConsole.print("can't find file of hotfix-rules.pro.");
			return false;
		}

		Properties properties = new Properties();
		properties.load(new FileInputStream(rulesFile));

		File clsDir = new File(proDir, getTargetDir(properties));
		List<File> clsFiles = new ArrayList<>();
		loadClassFiles(clsFiles, clsDir);

		if (clsFiles.isEmpty()) {
			LConsole.print(String.format("can't find any class file on %s.", clsDir));
			return false;
		}
		
		ClassPool classPool = ClassPool.getDefault();
		classPool.appendClassPath(clsDir.getPath());
		
		File hotfixDir = new File(proDir, "build-hotfix");
		hotfixDir.delete();

		for (File file : clsFiles) {
			String path = file.getPath().substring(clsDir.getPath().length() + 1);
			path = path.substring(0, path.length() - 6);
			String cls = path.replace("\\", ".");

			CtClass c = classPool.get(cls);
			
			c.stopPruning(true);
			if (c.isFrozen()) c.defrost();
			
			CtConstructor ctConstructor = c.getConstructors()[0];
			ctConstructor.insertAfter("System.out.println(com.linxcool.hotfix.AntilazyLoad.class);");
			c.writeFile(hotfixDir.getPath());
		}
		
		LConsole.print("finished insert code to class.");
		return true;
	}

	private String getTargetDir(Properties properties) {
		return properties.getProperty("dir", "bin/classes");
	}

	protected List<String> getTargetFilter(Properties properties) {
		String filter = properties.getProperty("filter", null);
		if (filter == null || filter.trim().length() == 0) {
			return null;
		}
		return Arrays.asList(filter.split(","));
	}

	protected void loadClassFiles(List<File> list, File current) {
		if (!current.exists()) {
			return;
		}
		if (current.isFile()) {
			if (current.getName().endsWith(".class")) {
				list.add(current);
			}
		} else {
			File[] files = current.listFiles();
			for (File file : files) {
				loadClassFiles(list, file);
			}
		}
	}

}
