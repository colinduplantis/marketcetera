package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.position.Grouping;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * This class maintains a summary position for an event list of positions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SummaryRowUpdater {

    private final EventList<PositionRow> mChildren;
    private final PositionRowImpl mPositionRow;
    private final ListEventListener<PositionRow> listChangeListener;

    /**
     * Constructor.
     * 
     * @param sourceValue
     *            the list to summarize, must have at least one element
     * @param grouping
     *            the current grouping level, cannot be null
     * @throws IllegalArgumentException
     *             if sourceValue is null or empty, or grouping is null
     */
    public SummaryRowUpdater(EventList<PositionRow> sourceValue, Grouping grouping) {
        Validate.notEmpty(sourceValue);
        Validate.notNull(grouping);
        mChildren = sourceValue;
        PositionRow row = sourceValue.get(0);
        // use the key data from the first row...it won't exactly match all rows, but it's
        // sufficient for further grouping
        mPositionRow = new PositionRowImpl(row.getSymbol(), row.getAccount(), row.getTraderId(),
                grouping.get(row), mChildren);
        listChangeListener = new ListEventListener<PositionRow>() {

            @Override
            public void listChanged(ListEvent<PositionRow> listChanges) {
                SummaryRowUpdater.this.listChanged(listChanges);
            }
        };
        recalculate();
        mChildren.addListEventListener(listChangeListener);
    }

    /**
     * Returns the dynamically updated summary row.
     * 
     * @return the dynamically updated summary row
     */
    public PositionRow getSummary() {
        return mPositionRow;
    }

    /**
     * Cleanup this object
     */
    public void dispose() {
        mChildren.removeListEventListener(listChangeListener);
    }

    private void listChanged(ListEvent<PositionRow> listChanges) {
        if (listChanges.getSourceList() != mChildren) {
            throw new IllegalStateException();
        }
        while (listChanges.next()) {
            final int changeIndex = listChanges.getIndex();
            final int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT) {
                // we can optimize on insert
                PositionRow row = mChildren.get(changeIndex);
                mPositionRow.setPositionMetrics(add(mPositionRow.getPositionMetrics(), row
                        .getPositionMetrics()));
            } else {
                // recompute summary from scratch
                recalculate();
            }
        }
    }

    private void recalculate() {
        PositionMetrics metrics = new PositionMetricsImpl(BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        for (PositionRow row : mChildren) {
            metrics = add(metrics, row.getPositionMetrics());
        }
        mPositionRow.setPositionMetrics(metrics);
    }

    private PositionMetrics add(PositionMetrics current, PositionMetrics augend) {
        // by contract, incoming position, position, and realizedPL on the row should not be null
        BigDecimal incomingPosition = current.getIncomingPosition().add(
                augend.getIncomingPosition());
        BigDecimal position = current.getPosition().add(augend.getPosition());
        BigDecimal positionPL = add(current.getPositionPL(), augend.getPositionPL());
        BigDecimal tradingPL = add(current.getTradingPL(), augend.getTradingPL());
        BigDecimal realizedPL = current.getRealizedPL().add(augend.getRealizedPL());
        BigDecimal unrealizedPL = add(current.getUnrealizedPL(), augend.getUnrealizedPL());
        BigDecimal totalPL = add(current.getTotalPL(), augend.getTotalPL());
        return new PositionMetricsImpl(incomingPosition, position, positionPL, tradingPL,
                realizedPL, unrealizedPL, totalPL);
    }

    private BigDecimal add(BigDecimal current, BigDecimal augend) {
        return current == null || augend == null ? null : current.add(augend);
    }

}
