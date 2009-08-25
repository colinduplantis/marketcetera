package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$") //$NON-NLS-1$
public class OutgoingMessageHolder extends MessageHolder {
	public OutgoingMessageHolder(Message message) {
		super(message);
	}
	public OutgoingMessageHolder(Message message, String groupID) {
		super(message, groupID);
	}
}