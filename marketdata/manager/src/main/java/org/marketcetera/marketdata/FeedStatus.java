package org.marketcetera.marketdata;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum FeedStatus
{
    /**
     * Feed is off-line -it may be started to be made available
     */
    OFFLINE,
    /**
     * Feed is off-line due to a problem - it may be restarted, but the start may not succeed 
     */
    ERROR,
    /**
     * Feed is available for requests
     */
    AVAILABLE,
    /**
     * Status of the feed is unknown
     */
    UNKNOWN;
}
