package eu.wwuk.eclipse.extsvcs.examples.client.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import eu.wwuk.eclipse.extsvcs.core.ComponentContext;
import eu.wwuk.eclipse.extsvcs.core.InjectedComponent;

public class EventReceivingView extends ViewPart implements InjectedComponent, EventHandler {

	private ComponentContext context;
	
	private Display display;
	private Text text;

	public void setComponentContext(ComponentContext context) {
		this.context = context;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		display = parent.getDisplay();
		
		text = new Text(parent, SWT.NONE);
		text.setText("Waiting");
	}
	@Override
	public void setFocus() {}

	public void handleEvent(final Event event) {
		System.out.println("Handling event: " + event.getTopic());
		display.asyncExec(new Runnable() {
			public void run() {
				if(!text.isDisposed()) {
					long time = System.currentTimeMillis();
					text.setText(event.getTopic() + " " + time);
				}
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		context.disposed();
	}
}
