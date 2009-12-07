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
package eu.wwuk.eclipse.extsvcs.examples.client;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView("eu.wwuk.eclipse.extsvcs.examples.views.log", IPageLayout.TOP, 1.0f, IPageLayout.ID_EDITOR_AREA);
		layout.addView("eu.wwuk.eclipse.extsvcs.examples.views.eventSending", IPageLayout.BOTTOM, 0.33f, "eu.wwuk.eclipse.extsvcs.examples.views.log");
		layout.addView("eu.wwuk.eclipse.extsvcs.examples.views.eventReceiving", IPageLayout.BOTTOM, 0.5f, "eu.wwuk.eclipse.extsvcs.examples.views.eventSending");
	}

}
