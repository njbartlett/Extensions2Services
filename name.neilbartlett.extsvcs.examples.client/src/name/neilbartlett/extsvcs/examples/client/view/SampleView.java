package name.neilbartlett.extsvcs.examples.client.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.log.LogService;

public class SampleView extends ViewPart {
	
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
}
