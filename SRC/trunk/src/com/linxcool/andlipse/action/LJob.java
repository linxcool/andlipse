package com.linxcool.andlipse.action;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;

import com.linxcool.andlipse.core.LConsole;
import com.linxcool.andlipse.core.LResource;

public abstract class LJob extends Job  {

	protected IWorkbenchWindow window;
	protected IProject project;
	
	public LJob(String name, IWorkbenchWindow window) {
		super(name);
		this.window = window;
		this.project = LResource.getProject(window);
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
			LConsole.print(e.getMessage());
			e.printStackTrace();
		}
		monitor.done();
		return Status.OK_STATUS;
	}
	
	protected void monitorProgress(final IProgressMonitor monitor, final long millis) {
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
