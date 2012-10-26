package org.marketcetera.api.symbolresolver.impl;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.api.symbolresolver.SymbolResolver;
import org.marketcetera.api.systemmodel.instruments.Instrument;
import org.marketcetera.core.trade.EquityImpl;

/* $License$ */

/**
 * Attempts to resolve symbols as {@link org.marketcetera.core.trade.EquityImpl} instruments.
 *
 * @version $Id: EquitySymbolResolver.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@Immutable
public class EquitySymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolver#resolve(java.lang.String)
     */
    @Override
    public Instrument resolve(String inSymbol)
    {
        return resolve(inSymbol,
                       null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolver#resolve(java.lang.String, java.lang.Object)
     */
    @Override
    public Instrument resolve(String inSymbol,
                              Object inContext)
    {
        return new EquityImpl(inSymbol);
    }
}
