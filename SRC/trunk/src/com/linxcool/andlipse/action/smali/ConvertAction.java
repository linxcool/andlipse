package com.linxcool.andlipse.action.smali;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import com.linxcool.andlipse.action.LAction;

public class ConvertAction extends LAction {

	@Override
	public void run(IAction action) {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ConvertJob job = new ConvertJob(window);
				job.schedule();
			}
		});
	}
}