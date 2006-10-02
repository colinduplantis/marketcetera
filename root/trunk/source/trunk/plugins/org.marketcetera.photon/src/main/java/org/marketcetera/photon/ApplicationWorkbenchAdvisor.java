package org.marketcetera.photon;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;

/**
 * Class required by the RCP to initialize the workbench.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/**
	 * Does nothing more than return a new {@link ApplicationWorkbenchWindowAdvisor}
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor(org.eclipse.ui.application.IWorkbenchWindowConfigurer)
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	public String getInitialWindowPerspectiveId() {
		return EquityPerspectiveFactory.ID;
	}

	/**
	 * Creates a new MainConsole and adds it to the ConsoleManager
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		
		// explicitly register ide- and resource-related adapters that rdt relies on
		WorkbenchAdapterBuilder.registerAdapters();
		
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { new MainConsole() });
	}

}
