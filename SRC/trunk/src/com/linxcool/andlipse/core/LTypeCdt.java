package com.linxcool.andlipse.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

public class LTypeCdt {

	public static boolean isICElement(Object obj) {
		return LTypeHelper.instanceOf(obj.getClass(), "org.eclipse.cdt.core.model.ICElement");
	}
	
	public static Object getCProjectByICElement(Object element) {
		return LTypeHelper.invoke(element, "getCProject");
	}
	
	public static IResource getIResourceByICElement(Object element) {
		return LTypeHelper.invoke(element, "getResource");
	}
	
	public static IProject getProjectByICProject(Object cProject) {
		return LTypeHelper.invoke(cProject, "getProject");
	}
	
}
