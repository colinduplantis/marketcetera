package org.marketcetera.quickfix.customfields;

import org.marketcetera.core.ClassVersion;
import quickfix.IntField;

/**
 * This is a custom field to be part of the MarketDataSnapshotFullRefresh
 * message that we enter to specify the number of remaining snapshots to come.
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class NoMarketDataSnapshots extends IntField  {
    public static final int FIELD = 7601; // arbitrary start

    public NoMarketDataSnapshots() {
        super(FIELD);
    }

    public NoMarketDataSnapshots(int value) {
        super(FIELD, value);
    }
}
