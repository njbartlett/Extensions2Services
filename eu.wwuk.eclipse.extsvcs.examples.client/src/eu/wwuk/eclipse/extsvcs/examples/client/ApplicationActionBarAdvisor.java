package eu.wwuk.eclipse.extsvcs.examples.client;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private IWorkbenchAction openPrefs;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	openPrefs = ActionFactory.PREFERENCES.create(window);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager windowMenu = new MenuManager("Window");
    	menuBar.add(windowMenu);
    	windowMenu.add(openPrefs);
    }
    
}
