package com.linxcool.andlipse.action.cls;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.linxcool.andlipse.action.LAction;

public class ConstructorAction extends LAction {

	private Shell shell;

	public void run(IAction action) {
		MessageDialog.openInformation(
			shell,
			"Andlipse",
			"Insert Code To Constructor was executed.");
	}

}
