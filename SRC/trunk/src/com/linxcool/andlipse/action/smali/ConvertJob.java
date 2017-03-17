package com.linxcool.andlipse.action.smali;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;

import com.linxcool.andlipse.action.LJob;
import com.linxcool.andlipse.core.LConsole;
import com.linxcool.andlipse.core.LResource;
import com.linxcool.andlipse.core.LResource.FileInfo;

public class ConvertJob extends LJob {

	private static final float TIME_MILLIS_OF_ONE_LENGTH_APK = 0.00424f;
	private static final float TIME_MILLIS_OF_ONE_LENGTH_JAR = 0.00314f;
	
	private IProject project;
	private File jarFile;
	private File apkFile;

	public ConvertJob(IWorkbenchWindow window) {
		super("ConvertJob", window);
		project = LResource.getProject(window);

		FileInfo jarInfo = LResource.getJarFile(window);
		FileInfo apkInfo = LResource.getApkFile(window);
		if (apkInfo != null && jarInfo == null) {
			apkFile = apkInfo.file;
		} else if (apkInfo == null && jarInfo != null) {
			jarFile = jarInfo.file;
		} else if (apkInfo != null && jarInfo != null) {
			if (apkInfo.level <= jarInfo.level)
				apkFile = apkInfo.file;
			else
				jarFile = jarInfo.file;
		}
	}

	@Override
	public boolean execute(IProgressMonitor monitor) throws Exception {
		LConsole.print("start convert file to smail...");

		if (apkFile != null) {
			LConsole.print("apk file is " + apkFile);
			monitorProgress(monitor, countPerMillis());
			
			String apkPath = apkFile.getAbsolutePath();
			DxHelper.decode(apkPath);
			
			LConsole.print("finished convert apk to smail.");
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			return true;
		}

		if (jarFile != null) {
			LConsole.print("jar file is " + jarFile, false);
			monitorProgress(monitor, countPerMillis());
			
			long start = System.currentTimeMillis();
			
			String jarPath = jarFile.getAbsolutePath();
			String dexPath = DxHelper.dex(jarPath);
			DxHelper.smali(dexPath);
			LResource.deleteFile(dexPath);
			
			System.out.println("real cost " + (System.currentTimeMillis() - start));
			
			LConsole.print("finished convert jar to smail.");
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			return true;
		}

		LConsole.print("error, can't find jar or apk file.");
		return false;
	}

	private long countPerMillis() {
		long size = 0;
		float oneMillis = 0;
		if(apkFile != null) {
			size = apkFile.length();
			oneMillis = TIME_MILLIS_OF_ONE_LENGTH_APK;
		} else {
			size = jarFile.length();
			oneMillis = TIME_MILLIS_OF_ONE_LENGTH_JAR;
		}
		
		return (long) (oneMillis * size / 100);
	}
	
	private void monitorProgress(final IProgressMonitor monitor, final long millis) {
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.submit(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(millis);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!monitor.isCanceled()) {
						monitor.subTask(i + "%");
						monitor.worked(1);
					}
				}
			}
		});
	}

}
