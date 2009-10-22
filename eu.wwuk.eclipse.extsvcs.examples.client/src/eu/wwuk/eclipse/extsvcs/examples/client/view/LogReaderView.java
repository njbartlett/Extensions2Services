package eu.wwuk.eclipse.extsvcs.examples.client.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.log.LogReaderService;

public class LogReaderView extends ViewPart {
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		// ...
	}
	@Override
	public void setFocus() {
	}

	public void setLogReaderService(LogReaderService log) {
		// ...
	}
	
	public void unsetLogReaderService(LogReaderService log) {
		// ...
	}
}
