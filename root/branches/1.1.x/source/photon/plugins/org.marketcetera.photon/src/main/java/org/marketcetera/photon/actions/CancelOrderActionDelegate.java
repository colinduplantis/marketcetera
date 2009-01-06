package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonController;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

/**
 * CancelOrderActionDelegate is a subclass of {@link ActionDelegate}
 * that is responsible for cancelling an order based on a selected
 * message with the appropriate {@link ClOrdID}.  This action is usually
 * invoked by the user by right clicking and choosing "Cancel" from
 * the context menu.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CancelOrderActionDelegate 
    extends ActionDelegate
    implements Messages
{
	public final static String ID = "org.marketcetera.photon.actions.CancelOrderActionDelegate"; //$NON-NLS-1$

	private IStructuredSelection selection;

	private PhotonController manager;

	/**
	 * Create a new {@link CancelOrderActionDelegate}
	 */
	public CancelOrderActionDelegate(){
	}
	
	/**
	 * Initializes this ActionDelegate with the specified action
	 * object.  Also gets the Application's OrderManager and stores
	 * it in an instance variable.
	 * @see org.eclipse.ui.actions.ActionDelegate#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction arg0) {
		this.manager = PhotonPlugin.getDefault().getPhotonController();
	}

	/**
	 * Does nothing
	 * @see org.eclipse.ui.actions.ActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/**
	 * Determines whether this action delegate should be
	 * enabled, depending on the value of the new selection.
	 * Checks the selectino to see if it is non-trivial, 
	 * and if it contains a FIX message of the appropriate
	 * type as its first element.
	 * 
	 * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection incoming) {
		boolean shouldEnable = false;
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			if (selection.size() == 1){
				Object firstElement = selection.getFirstElement();
				Message theMessage = null;
				if (firstElement instanceof Message) {
					theMessage = (Message) firstElement;
				} else if (firstElement instanceof ReportHolder){
					theMessage = ((ReportHolder) firstElement).getMessage();
				}
				if (theMessage != null && theMessage.isSetField(ClOrdID.FIELD) && (FIXMessageUtil.isCancellable(theMessage)) &&
						(FIXMessageUtil.isOrderSingle(theMessage) || FIXMessageUtil.isExecutionReport(theMessage))){
					shouldEnable = true;
				}
			}
		}
		action.setEnabled(shouldEnable);
	}

	/**
	 * Cancels the order specified by the FIX message in the selection,
	 * by extracting the {@link ClOrdID} and then calling {@link PhotonController#cancelOneOrderByClOrdID(String)}
	 * 
	 * @see org.eclipse.ui.actions.ActionDelegate#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction arg0, Event arg1) {
		Object item = selection.getFirstElement();
		Message qfMessage = null;
		if (item instanceof Message) {
			qfMessage = (Message) item;
			
		} else if (item instanceof ReportHolder) {
			ReportHolder holder = (ReportHolder) item;
			qfMessage = holder.getMessage();
		}
		if (qfMessage != null){
			try {
				this.manager.cancelOneOrderByClOrdID(qfMessage.getString(ClOrdID.FIELD));
			} catch (NoMoreIDsException e) {
				PhotonPlugin.getMainConsoleLogger().error(CANNOT_CANCEL.getText(),
				                                          e);
			} catch (FieldNotFound e) {
				PhotonPlugin.getMainConsoleLogger().error(MISSING_CLORDID.getText(),
				                                          e);
			}
		}
	}




}