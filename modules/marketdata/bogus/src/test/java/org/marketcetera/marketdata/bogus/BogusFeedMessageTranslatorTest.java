package org.marketcetera.marketdata.bogus;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.core.marketdata.Content;
import org.marketcetera.core.marketdata.DataRequestTranslator;
import org.marketcetera.core.marketdata.MarketDataMessageTranslatorTestBase;
import org.marketcetera.core.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Tests {@link BogusFeedMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedMessageTranslatorTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
public class BogusFeedMessageTranslatorTest
    extends MarketDataMessageTranslatorTestBase<MarketDataRequest>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getCapabilities()
     */
    @Override
    protected Set<Content> getCapabilities()
    {
        return Collections.unmodifiableSet(EnumSet.complementOf(EnumSet.of(Content.BBO10)));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#getTranslator()
     */
    @Override
    protected DataRequestTranslator<MarketDataRequest> getTranslator()
    {
        return BogusFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataMessageTranslatorTestBase#verifyResponse(java.lang.Object, java.lang.String, org.marketcetera.marketdata.MarketDataRequest.Content, org.marketcetera.marketdata.MarketDataRequest.Type, java.lang.String[])
     */
    @Override
    protected void verifyResponse(MarketDataRequest inActualResponse,
                                  String inExpectedExchange,
                                  Content[] inExpectedContent,
                                  String[] inExpectedSymbols)
            throws Exception
    {
        assertEquals(inExpectedExchange == null || inExpectedExchange.isEmpty() ? null : inExpectedExchange,
                inActualResponse.getExchange());
        assertArrayEquals(inExpectedContent,
                          inActualResponse.getContent().toArray(new Content[inActualResponse.getContent().size()]));
        assertArrayEquals(inExpectedSymbols,
                          inActualResponse.getSymbols().toArray(new String[0]));
    }
}
