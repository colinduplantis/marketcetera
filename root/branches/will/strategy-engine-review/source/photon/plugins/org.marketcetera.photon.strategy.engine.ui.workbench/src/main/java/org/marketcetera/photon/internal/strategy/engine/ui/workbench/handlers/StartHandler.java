package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.concurrent.Callable;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtils;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.start} command that
 * starts the currently selected strategy.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StartHandler extends SafeHandler {

    @Override
    public void executeSafely(ExecutionEvent event) throws ExecutionException {
        final DeployedStrategy strategy = (DeployedStrategy) ((IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event)).getFirstElement();
        final StrategyEngineConnection connection = strategy.getEngine()
                .getConnection();
        String name = strategy.getInstanceName();
        final IRunnableWithProgress operation = JFaceUtils.wrap(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        connection.start(strategy);
                        return null;
                    }
                }, Messages.START_HANDLER__TASK_NAME.getText(name));
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                new I18NBoundMessage1P(Messages.START_HANDLER_FAILED, name));
    }
}
