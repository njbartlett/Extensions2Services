package eu.wwuk.eclipse.extsvcs.examples.client;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.addView("eu.wwuk.eclipse.extsvcs.examples.client.sampleView", IPageLayout.TOP, 1f, IPageLayout.ID_EDITOR_AREA);
	}

}
