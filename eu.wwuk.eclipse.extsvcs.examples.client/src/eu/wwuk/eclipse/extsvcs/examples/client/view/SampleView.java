package eu.wwuk.eclipse.extsvcs.examples.client.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.log.LogService;

import eu.wwuk.eclipse.extsvcs.core.ComponentContext;
import eu.wwuk.eclipse.extsvcs.core.InjectedComponent;

public class SampleView extends ViewPart implements InjectedComponent {
	
	private LogService log;

	@Override
	public void createPartControl(Composite parent) {
		new Label(parent, SWT.NONE);
	}

	@Override
	public void setFocus() {
	}

	public void setLogService(LogService log) {
		System.out.println("setLogService() called");
		this.log = log;
	}
	
	public void unsetLogService(LogService log) {
		System.out.println("unsetLogService() called");
	}

	public void setComponentContext(ComponentContext context) {
		System.out.println("setComponentContext called");
	}
}
