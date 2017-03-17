package com.linxcool.andlipse.action.cls;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import com.linxcool.andlipse.action.LAction;

public class ConstructorAction extends LAction {

	public void run(IAction action) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ConstructorJob job = new ConstructorJob(window);
				job.schedule();
			}
		});
	}

}
