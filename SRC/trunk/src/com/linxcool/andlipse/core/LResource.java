package com.linxcool.andlipse.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;

@SuppressWarnings("restriction")
public class LResource {

	public static final String FILE_APK = ".apk";
	public static final String FILE_JAR = ".jar";
	public static final String FILE_DEX = ".dex";

	private static final String PREFERENCE_DIR = "bin";

	private static IProject project;

	/**
	 * 返回当前工作空间
	 * @return
	 */
	public static IPath getWorkspace() {
		return Platform.getLocation();
	}

	public static IStructuredSelection getSelection(IWorkbenchWindow window) {
		try {
			ISelectionService service = window.getSelectionService();
			ISelection selection = service.getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection)selection;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static IProject getProject(IWorkbenchWindow window) {
		IStructuredSelection selection = getSelection(window);
		if(selection == null) return project;

		Object element = selection.getFirstElement();
		if(element == null) {
			LConsole.print("selection first element is null");
			element = selection;
		}

		if (element instanceof IResource) {
			project = ((IResource) element).getProject();
		} else if (element instanceof PackageFragmentRootContainer) {
			IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
			project = jProject.getProject();
		} else if (element instanceof IJavaElement) {
			IJavaProject jProject = ((IJavaElement) element).getJavaProject();
			project = jProject.getProject();
		} else if (LTypeCdt.isICElement(element)) {
			Object cProject = LTypeCdt.getCProjectByICElement(element);
			project = LTypeCdt.getProjectByICProject(cProject);
		}

		return project;
	}

	public static IPath getLocation(IWorkbenchWindow window) {
		IStructuredSelection selection = getSelection(window);
		if(selection == null) return null;

		Object element = selection.getFirstElement();
		if(element == null) {
			LConsole.print("selection first element is null");
			element = selection;
		}

		if (element instanceof IResource) {
			return ((IResource) element).getLocation();
		} else if (element instanceof PackageFragmentRootContainer) {
			IJavaProject jProject = ((PackageFragmentRootContainer) element).getJavaProject();
			return jProject.getProject().getLocation();
		} else if (element instanceof IJavaElement) {
			return ((IJavaElement) element).getResource().getLocation();
		} else if (LTypeCdt.isICElement(element)) {
			return LTypeCdt.getIResourceByICElement(element).getLocation();
		}

		return null;
	}

	/**
	 * 获取当前空间下的首选jar包
	 * @param window
	 * @return
	 */
	public static FileInfo getJarFile(IWorkbenchWindow window) {
		return getTypeFile(window, FILE_JAR);
	}

	/**
	 * 获取当前空间下的首选APK包
	 * @param window
	 * @return
	 */
	public static FileInfo getApkFile(IWorkbenchWindow window) {
		return getTypeFile(window, FILE_APK);
	}

	/**
	 * 获取类型文件
	 * @param window
	 * @param fileType
	 * @return
	 */
	public static FileInfo getTypeFile(IWorkbenchWindow window, String fileType) {
		// 优先选中目录
		IPath selection = getLocation(window);
		if(selection != null) {
			File file = getFileInDirectory(selection.toFile(), fileType);
			if(file != null) return new FileInfo(file, 0, fileType);
		}
		
		// 再工程首选目录
		IProject project = getProject(window);
		if(project != null) {
			File proDir = project.getLocation().toFile();
			File binDir = new File(proDir, PREFERENCE_DIR);
			File file = getFileInDirectory(binDir, fileType);
			if(file != null) return new FileInfo(file, 1, fileType);
		}
		
		// 最后同级遍历
		File parentFile = selection.toFile().getParentFile();
		File file = getFileInDirectory(parentFile, fileType);
		if(file != null) return new FileInfo(file, 2, fileType);
		
		return null;
	}

	/**
	 * 从指定目录下获取指定文件
	 * @param parent
	 * @param fileType
	 * @return
	 */
	public static File getFileInDirectory(File parent, String fileType) {
		if(parent == null || !parent.exists())
			return null;
		if(parent.isFile()) {
			if(parent.getName().endsWith(fileType)) 
				return parent;
			return null;
		}
		File[] items = parent.listFiles();
		for (File item : items) {
			File file = getFileInDirectory(item, fileType);
			if(file != null) return file;
		}
		return null;
	}

	public static void deleteFile(String filePath) {
		deleteFile(new File(filePath));
	}
	
	/**
	 * 删除目标路径的文件
	 * @param path
	 * @return
	 */
	public static void deleteFile(File file) {
		if(file.exists() && file.isFile())
			file.delete();
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File subFile : files) {
				deleteFile(subFile);
			}
			file.delete();
		}
	}
	
	public static class FileInfo {
		
		public File file;
		public int level;
		public String type;
		
		public FileInfo(File file, int level, String type) {
			this.file = file;
			this.level = level;
			this.type = type;
		}
	}
}
