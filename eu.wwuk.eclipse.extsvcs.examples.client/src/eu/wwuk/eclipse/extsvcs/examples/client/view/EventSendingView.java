/*******************************************************************************
 * Copyright (c) 2009 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
package eu.wwuk.eclipse.extsvcs.examples.client.view;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import eu.wwuk.eclipse.extsvcs.core.ComponentContext;
import eu.wwuk.eclipse.extsvcs.core.InjectedComponent;

public class EventSendingView extends ViewPart implements InjectedComponent {

	private volatile ComponentContext context;

	public void setComponentContext(ComponentContext context) {
		this.context = context;
	}
	@Override
	public void createPartControl(Composite parent) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Send");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EventAdmin ea = (EventAdmin) context.locateService("EventAdmin");
				if (ea != null)
					ea.postEvent(new Event("PING", (Map) null));
			}
		});
	}
	@Override
	public void setFocus() { }
}
