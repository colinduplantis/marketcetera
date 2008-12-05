package org.marketcetera.ors.dest;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.ors.filters.MessageModifierManager;
import org.marketcetera.ors.filters.MessageRouteManager;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

/**
 * The in-memory representation of a single destination.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class Destination
{

    // CLASS DATA

    private static final String HEARTBEAT_CATEGORY=
        Destination.class.getName()+".HEARTBEATS";


    // INSTANCE DATA.

    private final SpringDestination mSpringDestination;
    private final DestinationID mDestinationID;
    private FIXDataDictionary mDataDictionary;
    private boolean mLoggedOn;


    // CONSTRUCTORS.

    /**
     * Creates a new destination based on the given configuration. Its
     * message modifiers are configured to rely on the given report
     * history services provider for persistence operations.
     *
     * @param springDestination The configuration.
     * @param historyServices The report history services provider.
     */

    public Destination
        (SpringDestination springDestination,
         ReportHistoryServices historyServices)
    {
        mSpringDestination=springDestination;
        mDestinationID=new DestinationID(getSpringDestination().getId());
        if (getModifiers()!=null) {
            getModifiers().setMessageFactory(getFIXMessageFactory());
            getModifiers().setHistoryServices(historyServices);
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's configuration.
     *
     * @return The configuration.
     */

    public SpringDestination getSpringDestination()
    {
        return mSpringDestination;
    }

    /**
     * Returns the receiver's status.
     *
     * @return The status.
     */

    public DestinationStatus getStatus()
    {
        return new DestinationStatus
            (getName(),getDestinationID(),getLoggedOn());
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return getSpringDestination().getName();
    }

    /**
     * Returns the receiver's destination ID.
     *
     * @return The ID.
     */

    public DestinationID getDestinationID()
    {
        return mDestinationID;
    }

    /**
     * Returns the receiver's QuickFIX/J session ID.
     *
     * @return The ID.
     */

    public SessionID getSessionID()
    {
        return getSpringDestination().getDescriptor().getQSessionID();
    }

    /**
     * Returns the receiver's QuickFIX/J session.
     *
     * @return The session.
     */

    public Session getSession()
    {
        return Session.lookupSession(getSessionID());
    }

    /**
     * Returns the receiver's QuickFIX/J data dictionary.
     *
     * @return The dictionary.
     */

    public DataDictionary getDataDictionary()
    {
        return getSession().getDataDictionary();
    }

    /**
     * Returns the receiver's message modifier manager.
     *
     * @return The manager. It may be null.
     */

    public MessageModifierManager getModifiers()
    {
        return getSpringDestination().getModifiers();
    }

    /**
     * Returns the receiver's route manager.
     *
     * @return The manager. It may be null.
     */

    public MessageRouteManager getRoutes()
    {
        return getSpringDestination().getRoutes();
    }

    /**
     * Returns the receiver's FIX version.
     *
     * @return The version.
     */

    public FIXVersion getFIXVersion()
    {
        return FIXVersion.getFIXVersion(getSessionID().getBeginString());
    }

    /**
     * Returns the receiver's FIX message factory.
     *
     * @return The factory.
     */

    public FIXMessageFactory getFIXMessageFactory()
    {
        return getFIXVersion().getMessageFactory();
    }

    /**
     * Returns the receiver's FIX data dictionary.
     *
     * @return The dictionary.
     */

    public synchronized FIXDataDictionary getFIXDataDictionary()
    {
        if (mDataDictionary==null) {
            mDataDictionary=new FIXDataDictionary(getDataDictionary());
        }
        return mDataDictionary;
    }

    /**
     * Returns the receiver's FIX message augmentor.
     *
     * @return The augmentor.
     */

    public FIXMessageAugmentor getFIXMessageAugmentor()
    {
        return getFIXMessageFactory().getMsgAugmentor();
    }

    /**
     * Sets the receiver's logon flag to the given value. This method
     * is synchronized to ensure that all threads will see the most
     * up-to-date value for the flag.
     *
     * @param loggedOn The flag.
     */

    public synchronized void setLoggedOn
        (boolean loggedOn)
    {
        mLoggedOn=loggedOn;
    }

    /**
     * Returns the receiver's logon flag. This method is synchronized
     * to ensure that all threads will see the most up-to-date value
     * for the flag.
     *
     * @return The flag.
     */

    public synchronized boolean getLoggedOn()
    {
        return mLoggedOn;
    }

    /**
     * Logs the given message, analyzed using the receiver's data
     * dictionary, at the debugging level.
     *
     * @param msg The message.
     */

    public void logMessage
        (Message msg)
    {
        Object category=(FIXMessageUtil.isHeartbeat(msg)?
                         HEARTBEAT_CATEGORY:this);
        if (SLF4JLoggerProxy.isDebugEnabled(category)) {
            Messages.ANALYZED_MESSAGE.debug
                (category,
                 new AnalyzedMessage(getDataDictionary(),msg).toString());
        }        
    }


    // Object.

    public String toString()
    {
        return Messages.DESTINATION_STRING.getText
            (getDestinationID().getValue(),getSessionID(),getName());
    }
}
