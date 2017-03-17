package com.linxcool.andlipse.action;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

public abstract class LJob extends Job  {

	protected IWorkbenchWindow window;
	
	public LJob(String name, IWorkbenchWindow window) {
		super(name);
		this.window = window;
	}

	/**
	 * 注意该方法在后台调用。
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean execute(IProgressMonitor monitor)
			throws Exception;
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Start Task", 100); 
		try {
			execute(monitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
}
