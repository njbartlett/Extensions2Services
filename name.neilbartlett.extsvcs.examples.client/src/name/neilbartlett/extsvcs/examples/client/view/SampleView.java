package name.neilbartlett.extsvcs.examples.client.view;

import name.neilbartlett.extsvcs.core.ComponentContext;
import name.neilbartlett.extsvcs.core.InjectedComponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.log.LogService;

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
