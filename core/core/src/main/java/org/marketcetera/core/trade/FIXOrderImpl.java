package org.marketcetera.core.trade;

import javax.xml.bind.annotation.XmlRootElement;
import quickfix.Message;

/* $License$ */
/**
 * Message that wraps a FIX Message. This class is public for the sake
 * of JAXB and is not intended for general use.
 *
 * @author anshul@marketcetera.com
 * @version $Id: FIXOrderImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@XmlRootElement
public class FIXOrderImpl extends FIXMessageWrapper implements FIXOrder {

    @Override
    public SecurityType getSecurityType() {
        return FIXUtil.getSecurityType(getMessage());
    }

    @Override
    public BrokerID getBrokerID() {
        return mBrokerID;
    }

    @Override
    public void setBrokerID(BrokerID inBrokerID) {
        if(inBrokerID == null) {
            throw new NullPointerException();
        }
        mBrokerID = inBrokerID;
    }
    /**
     * Creates an instance.
     *
     * @param inMessage The FIX Message instance. Cannot be null.
     * @param inBrokerID the ID of the broker to which
     * this order should be sent. Cannot be null.
     */
    FIXOrderImpl(Message inMessage, BrokerID inBrokerID) {
        super(inMessage);
        if(inMessage == null || inBrokerID == null) {
            throw new NullPointerException();
        }
        mBrokerID = inBrokerID;
    }

    /**
     * Creates an uninitialized instance. This constructor is meant to be
     * used by JAXB.
     */
    @SuppressWarnings("unused")
    private FIXOrderImpl() {
    }

    @Override
    public synchronized String toString() {
        return Messages.FIX_ORDER_TO_STRING.getText(
                getBrokerID().getValue(),
                String.valueOf(getMessage()));
    }

    private BrokerID mBrokerID;
    private static final long serialVersionUID = 1L;
}
