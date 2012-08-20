package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.ConvertibleBond;

/* $License$ */

/**
 * Compares positions of <code>ConvertibleBond</code> instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondPositionKeyComparator
        extends InstrumentPositionKeyComparator<ConvertibleBond>
{
    /**
     * Create a new ConvertibleBondPositionKeyComparator instance.
     */
    public ConvertibleBondPositionKeyComparator()
    {
        super(ConvertibleBond.class);
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ConvertibleBond inBond1,
                       ConvertibleBond inBond2)
    {
        return new CompareToBuilder().append(inBond1.getSymbol(),
                                             inBond2.getSymbol()).toComparison();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.InstrumentPositionKeyComparator#getRank()
     */
    @Override
    public int getRank()
    {
        return 400;
    }
}
