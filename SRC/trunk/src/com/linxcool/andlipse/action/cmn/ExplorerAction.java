package com.linxcool.andlipse.action.cmn;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;

import com.linxcool.andlipse.action.LAction;
import com.linxcool.andlipse.core.LCommand;
import com.linxcool.andlipse.core.LResource;

public class ExplorerAction extends LAction {

	@Override
	public void run(IAction action) {
		IPath location = LResource.getLocation(window);
		if(location == null) {
			showMessage("did't select any resource");
			return;
		}
		
		File file = location.toFile();
		String path = file.getAbsolutePath();
		if(file.isFile()) {
			path = file.getParentFile().getAbsolutePath();
		}
		
		try {
			LCommand.exec("explorer.exe " + path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
