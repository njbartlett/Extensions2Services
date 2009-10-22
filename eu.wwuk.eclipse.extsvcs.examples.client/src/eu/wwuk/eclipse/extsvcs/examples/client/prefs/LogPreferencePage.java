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
package eu.wwuk.eclipse.extsvcs.examples.client.prefs;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.log.LogService;

public class LogPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private final AtomicReference<LogService> logRef = new AtomicReference<LogService>(null);
	
	private Composite container;
	private Label label;
	private Button button;
	
	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		
		label = new Label(container, SWT.NONE);
		
		button = new Button(container, SWT.PUSH);
		button.setText("Write to Log");
		
		updateLabelAndButton();
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				LogService log = logRef.get();
				if(log != null) log.log(LogService.LOG_ERROR, "This is a message from the button");
			}
		});
		
		container.setLayout(new GridLayout(2, false));
		
		return container;
	}

	public void init(IWorkbench workbench) {
	}
	
	private void updateLabelAndButton() {
		LogService log = logRef.get();
		if(log != null) {
			button.setEnabled(true);
			label.setText("LogService AVAILABLE");
		} else {
			button.setEnabled(false);
			label.setText("LogService Unavailable");
		}
	}
	
	public void setLogService(LogService log) {
		logRef.set(log);
		if(container != null && !container.isDisposed()) container.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateLabelAndButton();
			}
		});
	}
	public void unsetLogService(LogService log) {
		logRef.compareAndSet(log, null);
		if(container != null && !container.isDisposed()) container.getDisplay().asyncExec(new Runnable() {
			public void run() {
				updateLabelAndButton();
			}
		});
	}
}
