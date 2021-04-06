//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.dao;

/* $License$ */

/**
 * Indicates a trade that took place at a particular point in time for a particular amount and price.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@javax.persistence.Entity(name="Trade")
@javax.persistence.Table(name="trades")
public class PersistentTrade
        extends org.marketcetera.persist.EntityBase
        implements org.marketcetera.trade.pnl.Trade,org.marketcetera.trade.HasInstrument
{
    /**
     * Create a new PersistentTrade instance.
     */
    public PersistentTrade() {}
    /**
     * Create a new PersistentTrade instance.
     *
     * @param inTrade a <code>Trade</code> value
     */
    public PersistentTrade(org.marketcetera.trade.pnl.Trade inTrade)
    {
        setInstrument(inTrade.getInstrument());
        setExecutionId(inTrade.getExecutionId());
        setPrice(inTrade.getPrice());
        setQuantity(inTrade.getQuantity());
        setTransactionTime(inTrade.getTransactionTime());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public org.marketcetera.trade.Instrument getInstrument()
    {
        initInstrument();
        return instrument;
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    @Override
    public void setInstrument(org.marketcetera.trade.Instrument inInstrument)
    {
        instrument = inInstrument;
        if(inInstrument == null) {
            securityType = null;
            symbol = null;
            strikePrice = null;
            optionType = null;
            expiry = null;
        } else {
            securityType = inInstrument.getSecurityType();
            symbol = inInstrument.getFullSymbol();
            if(inInstrument.getSecurityType().equals(org.marketcetera.trade.SecurityType.Option)) {
                strikePrice = ((org.marketcetera.trade.Option)inInstrument).getStrikePrice();
                optionType = ((org.marketcetera.trade.Option)inInstrument).getType();
                expiry = ((org.marketcetera.trade.Option)inInstrument).getExpiry();
            }
        }
    }
    /**
     * Get the executionId value.
     *
     * @return a <code>org.marketcetera.trade.OrderID</code> value
     */
    @Override
    public org.marketcetera.trade.OrderID getExecutionId()
    {
        return executionId;
    }
    /**
     * Set the executionId value.
     *
     * @param inExecutionId a <code>org.marketcetera.trade.OrderID</code> value
     */
    @Override
    public void setExecutionId(org.marketcetera.trade.OrderID inExecutionId)
    {
        executionId = inExecutionId;
    }
    /**
     * Get the price value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    @Override
    public java.math.BigDecimal getPrice()
    {
        return price;
    }
    /**
     * Set the price value.
     *
     * @param inPrice a <code>java.math.BigDecimal</code> value
     */
    @Override
    public void setPrice(java.math.BigDecimal inPrice)
    {
        price = inPrice == null ? java.math.BigDecimal.ZERO : inPrice;
    }
    /**
     * Get the quantity value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    @Override
    public java.math.BigDecimal getQuantity()
    {
        return quantity;
    }
    /**
     * Set the quantity value.
     *
     * @param inQuantity a <code>java.math.BigDecimal</code> value
     */
    @Override
    public void setQuantity(java.math.BigDecimal inQuantity)
    {
        quantity = inQuantity == null ? java.math.BigDecimal.ZERO : inQuantity;
    }
    /**
     * Get the transactionTime value.
     *
     * @return a <code>java.util.Date</code> value
     */
    @Override
    public java.util.Date getTransactionTime()
    {
        return transactionTime;
    }
    /**
     * Set the transactionTime value.
     *
     * @param inTransactionTime a <code>java.util.Date</code> value
     */
    @Override
    public void setTransactionTime(java.util.Date inTransactionTime)
    {
        transactionTime = inTransactionTime;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Trade [")
            .append("instrument=").append(instrument)
            .append(", executionId=").append(executionId)
            .append(", price=").append(org.marketcetera.core.BigDecimalUtil.renderCurrency(price))
            .append(", quantity=").append(org.marketcetera.core.BigDecimalUtil.render(quantity))
            .append(", transactionTime=").append(transactionTime).append("]");
        return builder.toString();
    }
    /**
     * Sets the value of the instrument, if necessary.
     */
    private void initInstrument()
    {
        if(instrument == null) {
            instrument = org.marketcetera.core.PlatformServices.getInstrument(symbol);
        }
    }
    /**
     * instrument value
     */
    private transient org.marketcetera.trade.Instrument instrument;
    /**
     * symbol value
     */
    @javax.persistence.Column(name="symbol",nullable=false)
    private String symbol;
    /**
     * strike price value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="strike_price",nullable=false,precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE)
    private java.math.BigDecimal strikePrice;
    /**
    * security type value
     */
    @javax.persistence.Column(name="security_type",nullable=false)
    private org.marketcetera.trade.SecurityType securityType;
    /**
     * expiry value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="expiry",nullable=true)
    private String expiry;
    /**
    * option type value, <code>null</code> for non-option types
     */
    @javax.persistence.Column(name="option_type",nullable=true)
    private org.marketcetera.trade.OptionType optionType;
    /**
     * exchange execution id that uniquely identifies this trade
     */
    @javax.persistence.Column(name="execution_id",nullable=true,unique=false)
    private org.marketcetera.trade.OrderID executionId;
    /**
     * price at which trade occurred
     */
    @javax.persistence.Column(name="price",precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE,nullable=true,unique=false)
    private java.math.BigDecimal price = java.math.BigDecimal.ZERO;
    /**
     * size of trade
     */
    @javax.persistence.Column(name="quantity",precision=org.marketcetera.core.PlatformServices.DECIMAL_PRECISION,scale=org.marketcetera.core.PlatformServices.DECIMAL_SCALE,nullable=true,unique=false)
    private java.math.BigDecimal quantity = java.math.BigDecimal.ZERO;
    /**
     * transaction date
     */
    @javax.persistence.Column(name="transaction_time",nullable=true,unique=false)
    private java.util.Date transactionTime;
    private static final long serialVersionUID = -1169854614L;
}
