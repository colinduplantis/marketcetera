package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.util.EventValidationServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link MarketDataEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class MarketDataBean
        extends EventBean
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public final Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public final void setInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
    }
    /**
     * Get the exchangeTimestamp value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchangeTimestamp()
    {
        return exchangeTimestamp;
    }
    /**
     * Sets the exchangeTimestamp value.
     *
     * @param a <code>String</code> value
     */
    public final void setExchangeTimestamp(String inExchangeTimestamp)
    {
        exchangeTimestamp = inExchangeTimestamp;
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getPrice()
    {
        return price;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setPrice(BigDecimal inPrice)
    {
        price = inPrice;
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getSize()
    {
        return size;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setSize(BigDecimal inSize)
    {
        size = inSize;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchange()
    {
        return exchange;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws IllegalArgumentException if {@link #getTimestamp()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getMessageId()} &lt; 0
     * @throws IllegalArgumentException if {@link #getInstrument()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getPrice()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getSize()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getExchange()} is <code>null</code>
     * @throws IllegalArgumentException if {@link #getExchangeTimestamp()} is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        instrument.validate();
        if(price == null) {
            EventValidationServices.error(VALIDATION_NULL_PRICE);
        }
        if(size == null) {
            EventValidationServices.error(VALIDATION_NULL_SIZE);
        }
        if(exchange == null) {
            EventValidationServices.error(VALIDATION_NULL_EXCHANGE);
        }
        if(exchangeTimestamp == null) {
            EventValidationServices.error(VALIDATION_NULL_EXCHANGE_TIMESTAMP);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((exchangeTimestamp == null) ? 0 : exchangeTimestamp.hashCode());
        result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((size == null) ? 0 : size.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketDataBean [exchange=").append(exchange).append(", exchangeTimestamp=")
                .append(exchangeTimestamp).append(", instrument=").append(instrument).append(", price=").append(price)
                .append(", size=").append(size).append(", getMessageId()=").append(getMessageId())
                .append(", getSource()=").append(getSource()).append(", getTimestamp()=").append(getTimestamp())
                .append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketDataBean other = (MarketDataBean) obj;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (exchangeTimestamp == null) {
            if (other.exchangeTimestamp != null)
                return false;
        } else if (!exchangeTimestamp.equals(other.exchangeTimestamp))
            return false;
        if (instrument == null) {
            if (other.instrument != null)
                return false;
        } else if (!instrument.equals(other.instrument))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (size == null) {
            if (other.size != null)
                return false;
        } else if (!size.equals(other.size))
            return false;
        return true;
    }
    /**
     * the market data price
     */
    private BigDecimal price;
    /**
     * the market data size
     */
    private BigDecimal size;
    /**
     * the market data exchange
     */
    private String exchange;
    /**
     * the market data exchange timestamp (format is dependent on the market data provider)
     */
    private String exchangeTimestamp;
    /**
     * the market data instrument
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private static final long serialVersionUID = 1L;
}
