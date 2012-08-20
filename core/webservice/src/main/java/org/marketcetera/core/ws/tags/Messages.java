package org.marketcetera.core.ws.tags;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage0P;
import org.marketcetera.core.util.log.I18NMessage1P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: Messages.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_ws_tags"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P SESSION_EXPIRED=
        new I18NMessage1P(LOGGER,"session_expired"); //$NON-NLS-1$
    static final I18NMessage0P SESSION_REQUIRED=
        new I18NMessage0P(LOGGER,"session_required"); //$NON-NLS-1$
}
