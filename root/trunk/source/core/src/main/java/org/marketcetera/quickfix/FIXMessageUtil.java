package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;
import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.StringField;
import quickfix.field.EncodedText;
import quickfix.field.MsgType;
import quickfix.field.Text;

/**
 * Collection of utilities to create work with FIX messages
 *
 * @author gmiller
 *         $Id$
 */
@ClassVersion("$Id$")
public class FIXMessageUtil {

    private static final String LOGGER_NAME = FIXMessageUtil.class.getName();
    private static final int MAX_FIX_FIELDS = 2000;     // What we think the ID of the last fix field is

    /**
     * Creates a new instance of FIXMessageUtil
     */
    public FIXMessageUtil() {
    }

    private static boolean msgTypeHelper(Message fixMessage, String msgType) {
    	if (fixMessage != null){
	    	try {
	            MsgType msgTypeField = new MsgType();
	            Header header = fixMessage.getHeader();
				if (header.isSetField(msgTypeField)){
	            	header.getField(msgTypeField);
	            	return msgType.equals(msgTypeField.getValue());
	            }
	        } catch (Exception ignored) {
                // ignored
            }
    	}
        return false;
    }

    public static boolean isExecutionReport(Message message) {
        return msgTypeHelper(message, MsgType.EXECUTION_REPORT);
    }

    public static boolean isOrderSingle(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_SINGLE);
    }

    public static boolean isReject(Message message) {
        return msgTypeHelper(message, MsgType.REJECT);
    }

    public static boolean isCancelReject(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REJECT);
    }

    public static boolean isStatusRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_STATUS_REQUEST);
    }

    public static boolean isCancelRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REQUEST);
    }

    public static boolean isCancelReplaceRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static boolean isOrderList(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_LIST);
    }

    public static boolean isLogon(Message message){
    	return msgTypeHelper(message, MsgType.LOGON);
    }

    public static boolean isLogout(Message message){
    	return msgTypeHelper(message, MsgType.LOGOUT);
    }

    /** Helper method to extract all useful fields from an existing message into another message
     * This is usually called when the "existing" message is malformed and is missing some fields,
     * and an appropriate "reject" message needs to be sent.
     * Can't say we are proud of this method - it's rather a kludge.
     * Goes through all the required fields in "outgoing" message, and ignores any missing ones
     * Skips over any of the outgoing fields that have already been set
     *
     * Use cases: an order comes in missing a Side, so we need to create an ExecutionReport
     * that's a rejection, and need to extract all the other fields (ClOrdId, size, etc)
     * which may or may not be present since the order is malformed
     *
     * @param outgoingMessage
     * @param existingMessage
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage)
    {
        try {
            String msgType = outgoingMessage.getHeader().getString(MsgType.FIELD);
            DataDictionary dict = FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary();
            for (int fieldInt = 1; fieldInt < MAX_FIX_FIELDS; fieldInt++){
                if (dict.isRequiredField(msgType, fieldInt) && existingMessage.isSetField(fieldInt) &&
                        !outgoingMessage.isSetField(fieldInt)){
                    try {
                        outgoingMessage.setField(existingMessage.getField(new StringField(fieldInt)));
                    } catch (FieldNotFound e) {
                        // do nothing and ignore
                    }
                }
            }

        } catch (FieldNotFound ex) {
            LoggerAdapter.error(MessageKey.FIX_OUTGOING_NO_MSGTYPE.getLocalizedMessage(), ex, LOGGER_NAME);
        }
    }

	public static void insertFieldIfMissing(int fieldNumber, String value, FieldMap fieldMap) throws MarketceteraException {
		if (fieldMap.isSetField(fieldNumber)){
			throw new MarketceteraException("Field "+fieldNumber+" is already set in message.");
		} else {
			fieldMap.setField(new StringField(fieldNumber, value));
		}
	}

	public static String getTextOrEncodedText(Message aMessage, String defaultString) {
		String text = defaultString;
		if (aMessage.isSetField(Text.FIELD)){
			try {
				text = aMessage.getString(Text.FIELD);
			} catch (FieldNotFound e) {
			}
		} else {
			try {
				text = aMessage.getString(EncodedText.FIELD);
			} catch (FieldNotFound e) {
			}
		}
		return text;
	}


}
