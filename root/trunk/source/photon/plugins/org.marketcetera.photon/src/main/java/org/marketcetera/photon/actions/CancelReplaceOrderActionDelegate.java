package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonController;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrderID;

/**
 * CancelReplaceOrderActionDelegate is a subclass of {@link ActionDelegate}
 * that is responsible for cancelling an order and sending an updated one based on a selected
 * message with the appropriate {@link ClOrdID}.  This action is usually
 * invoked by the user by right clicking and choosing "Cancel/Replace" from
 * the context menu.
 * 
 * @author gmiller
 * @version $Id$
 * @since $Release$
 *
 */
@ClassVersion("$Id$")
public class CancelReplaceOrderActionDelegate
    extends ActionDelegate
    implements Messages
{
	public final static String ID = "org.marketcetera.photon.actions.CancelReplaceOrderActionDelegate"; //$NON-NLS-1$

	private IStructuredSelection selection;

//	private PhotonController controller;

	private FIXMessageFactory messageFactory;

	/**
	 * Create a new {@link CancelReplaceOrderActionDelegate}
	 */
	public CancelReplaceOrderActionDelegate(){
	}
	
	/**
	 * Initializes this ActionDelegate with the specified action
	 * object.  Also gets the Application's OrderManager and stores
	 * it in an instance variable.
	 * @see org.eclipse.ui.actions.ActionDelegate#init(org.eclipse.jface.action.IAction)
	 */
	public void init(IAction arg0) {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
//		this.controller = plugin.getPhotonController();
		this.messageFactory = plugin.getMessageFactory();
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
						FIXMessageUtil.isExecutionReport(theMessage)){
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
		Message oldMessage = null;
		if (item instanceof Message) {
			oldMessage = (Message) item;
			
		} else if (item instanceof ReportHolder) {
			ReportHolder holder = (ReportHolder) item;
			oldMessage = holder.getMessage();
		}
		if (oldMessage != null){
			try {
				org.marketcetera.trade.OrderID clOrdId = new org.marketcetera.trade.OrderID(oldMessage.getString(ClOrdID.FIELD));
				ReportHolder originalReport = getOriginalOrderMessage(clOrdId);
				Message originalOrderMessage = originalReport == null ? null : originalReport.getMessage();
				if (originalOrderMessage == null) {
					originalOrderMessage = oldMessage;
				}
				String orderID = null;
				try {
					TradeReportsHistory fixMessageHistory = PhotonPlugin.getDefault().getTradeReportsHistory();
					ReportBase latestMessage = fixMessageHistory.getLatestExecutionReport(new org.marketcetera.trade.OrderID(clOrdId.getValue()));
					orderID = latestMessage.getOrderID().getValue();
				} catch (Exception ex){
					// use null
				}

				Message cancelReplaceMessage = messageFactory
						.newCancelReplaceFromMessage(originalOrderMessage);
				if (orderID != null){
					cancelReplaceMessage.setField(new OrderID(orderID));
				}
				IOrderTicketController controller = PhotonPlugin.getDefault()
						.getOrderTicketController(originalOrderMessage);
				if (controller != null) {
					controller.setOrderMessage(cancelReplaceMessage);
					controller.setBrokerId(originalReport.getBrokerID() == null ? null : originalReport.getBrokerID().getValue());
				}
			} catch (FieldNotFound e) {
                PhotonPlugin.getMainConsoleLogger().error(CANNOT_CANCEL.getText(),
                                                          e);
			}
		}
	}

	private ReportHolder getOriginalOrderMessage(org.marketcetera.trade.OrderID clOrdId) {
		TradeReportsHistory messageHistory = PhotonPlugin.getDefault()
				.getTradeReportsHistory();
		return messageHistory.getFirstReport(clOrdId);
	}

}
