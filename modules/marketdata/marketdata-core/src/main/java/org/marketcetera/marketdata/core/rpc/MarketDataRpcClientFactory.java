package org.marketcetera.marketdata.core.rpc;

import org.marketcetera.marketdata.core.MarketDataClientFactory;
import org.marketcetera.rpc.client.RpcClientFactory;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Creates <code>MarketDataServiceRpcClient</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class MarketDataRpcClientFactory
        implements RpcClientFactory<MarketDataRpcClientParameters,MarketDataRpcClient>,
                   MarketDataClientFactory<MarketDataRpcClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.rpc.client.RpcClientFactory#create(org.marketcetera.rpc.client.RpcClientParameters)
     */
    @Override
    public MarketDataRpcClient create(MarketDataRpcClientParameters inParameters)
    {
        return new MarketDataRpcClient(inParameters);
    }
}
