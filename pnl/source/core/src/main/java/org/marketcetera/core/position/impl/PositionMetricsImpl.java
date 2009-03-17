package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simple immutable implementation of {@link PositionMetrics}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class PositionMetricsImpl implements PositionMetrics {

    private final BigDecimal mIncomingPosition;
    private final BigDecimal mPosition;
    private final BigDecimal mPositionPL;
    private final BigDecimal mTradingPL;
    private final BigDecimal mRealizedPL;
    private final BigDecimal mUnrealizedPL;
    private final BigDecimal mTotalPL;

    /**
     * Convenience constructor for an empty position.
     */
    PositionMetricsImpl() {
        this(BigDecimal.ZERO);
    }

    /**
     * Convenience constructor for metrics that reflect a single incoming position.
     * 
     * @param incomingPosition
     *            the incoming position, cannot be null
     * @throws IllegalArgumentException
     *             if incoming position is null
     */
    PositionMetricsImpl(BigDecimal incomingPosition) {
        this(incomingPosition, incomingPosition, null, null, BigDecimal.ZERO, null, null);
    }

    /**
     * Constructor initializing all fields.
     * 
     * @param incomingPosition
     *            the incoming position, cannot be null
     * @param position
     *            the position, cannot be null
     * @param positionPL
     *            the position P&L value
     * @param tradingPL
     *            the trading P&L value
     * @param realizedPL
     *            the realized P&L value, cannot be null
     * @param unrealizedPL
     *            the unrealized P&L value
     * @param totalPL
     *            the total P&L value
     * @throws IllegalArgumentException
     *             if incoming position, position or realizedPL is null
     */
    PositionMetricsImpl(BigDecimal incomingPosition, BigDecimal position, BigDecimal positionPL,
            BigDecimal tradingPL, BigDecimal realizedPL, BigDecimal unrealizedPL, BigDecimal totalPL) {
        Validate.noNullElements(new Object[] { incomingPosition, position, realizedPL });
        this.mIncomingPosition = incomingPosition;
        this.mPosition = position;
        this.mPositionPL = positionPL;
        this.mTradingPL = tradingPL;
        this.mRealizedPL = realizedPL;
        this.mUnrealizedPL = unrealizedPL;
        this.mTotalPL = totalPL;
    }

    @Override
    public BigDecimal getIncomingPosition() {
        return mIncomingPosition;
    }

    @Override
    public BigDecimal getPosition() {
        return mPosition;
    }

    @Override
    public BigDecimal getPositionPL() {
        return mPositionPL;
    }

    @Override
    public BigDecimal getTradingPL() {
        return mTradingPL;
    }

    @Override
    public BigDecimal getRealizedPL() {
        return mRealizedPL;
    }

    @Override
    public BigDecimal getUnrealizedPL() {
        return mUnrealizedPL;
    }

    @Override
    public BigDecimal getTotalPL() {
        return mTotalPL;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(
                "position", mPosition) //$NON-NLS-1$
                .append("positionPL", mPositionPL) //$NON-NLS-1$
                .append("tradingPL", mTradingPL) //$NON-NLS-1$
                .append("realizedPL", mRealizedPL) //$NON-NLS-1$
                .append("unrealizedPL", mUnrealizedPL) //$NON-NLS-1$
                .append("totalPL", mTotalPL) //$NON-NLS-1$
                .toString();
    }
}