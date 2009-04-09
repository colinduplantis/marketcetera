package org.marketcetera.marketdata;

import static org.marketcetera.core.Util.KEY_VALUE_DELIMITER;
import static org.marketcetera.core.Util.KEY_VALUE_SEPARATOR;
import static org.marketcetera.marketdata.MarketDataRequest.Content.OHLC;
import static org.marketcetera.marketdata.MarketDataRequest.Content.TOP_OF_BOOK;
import static org.marketcetera.marketdata.MarketDataRequest.Type.SUBSCRIPTION;
import static org.marketcetera.marketdata.Messages.EXTRA_DATE;
import static org.marketcetera.marketdata.Messages.INVALID_CONTENT;
import static org.marketcetera.marketdata.Messages.INVALID_DATE;
import static org.marketcetera.marketdata.Messages.INVALID_REQUEST;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.INVALID_TYPE;
import static org.marketcetera.marketdata.Messages.MISSING_CONTENT;
import static org.marketcetera.marketdata.Messages.MISSING_PROVIDER;
import static org.marketcetera.marketdata.Messages.MISSING_SYMBOLS;
import static org.marketcetera.marketdata.Messages.MISSING_TYPE;
import static org.marketcetera.marketdata.Messages.OHLC_NO_DATE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.Util;
import org.marketcetera.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 * Represents a market data request.
 * 
 * <p>The market data request represented by this object may be constructed incrementally.  As such, the
 * request may or may not always be in a consistent, valid state.  To make sure that all internal consistency
 * requirements are met, invoke {@link #validate(MarketDataRequest)}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class MarketDataRequest
    implements Serializable
{
    /**
     * the delimiter used to distinguish between symbols in the string representation of the symbol collection
     */
    public static final String SYMBOL_DELIMITER = ","; //$NON-NLS-1$
    /**
     * the key used to identify the symbols in the string representation of the market data request
     */
    public static final String SYMBOLS_KEY = "symbols"; //$NON-NLS-1$
    /**
     * the key used to identify the provider in the string representation of the market data request
     */
    public static final String PROVIDER_KEY = "provider"; //$NON-NLS-1$
    /**
     * the key used to identify the content in the string representation of the market data request
     */
    public static final String CONTENT_KEY = "content"; //$NON-NLS-1$
    /**
     * the key used to identify the exchange in the string representation of the market data request
     */
    public static final String EXCHANGE_KEY = "exchange"; //$NON-NLS-1$
    /**
     * the key used to identify the date in the string representation of the market data request
     */
    public static final String DATE_KEY = "date"; //$NON-NLS-1$
    /**
     * the key used to identify the type in the string representation of the market data request
     */
    public static final String TYPE_KEY = "type"; //$NON-NLS-1$
    /**
     * Creates a <code>MarketDataRequest</code>.
     * 
     * <p>The <code>String</code> parameter should be a set of key/value pairs delimited
     * by {@link Util#KEY_VALUE_DELIMITER}.  The set of keys that this method understands
     * is as follows:
     * <ul>
     *   <li>{@link #SYMBOLS_KEY} - the symbols for which to request market data</li>
     *   <li>{@link #PROVIDER_KEY} - the provider from which to request market data</li>
     *   <li>{@link #CONTENT_KEY} - the content of the market data</li>
     *   <li>{@link #TYPE_KEY} - the type of market data request</li>
     *   <li>{@link #EXCHANGE_KEY} - the exchange for which to request market data</li>
     *   <li>{@link #DATE_KEY} - the date for which to request market data, if applicable</li>
     * </ul>
     * 
     * <p>Example:
     * <pre>
     * "symbols=GOOG,ORCL,MSFT:provider=marketcetera:content=TOP_OF_BOOK"
     * </pre>
     * 
     * <p>The key/value pairs are validated according to the rules established for each
     * component.  Extraneous key/value pairs, i.e., key/value pairs with a key that
     * does not match one of the above list are ignored.  Additional validation is performed
     * according to the rules defined at {@link Util#propertiesFromString(String)}.  
     *
     * @param inRequest a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the request cannot be constructed
     */
    public static MarketDataRequest newRequestFromString(String inRequest)
        throws MarketDataRequestException
    {
        try {
            Properties props = Util.propertiesFromString(inRequest);
            Map<String,String> sanitizedProps = new HashMap<String,String>();
            for(Object key : props.keySet()) {
                sanitizedProps.put(((String)key).toLowerCase(),
                                   ((String)props.get(key)).trim());
            }
            MarketDataRequest request = new MarketDataRequest();
            if(sanitizedProps.containsKey(SYMBOLS_KEY)) {
                request.setSymbols(sanitizedProps.get(SYMBOLS_KEY).split(SYMBOL_DELIMITER));
            }
            if(sanitizedProps.containsKey(PROVIDER_KEY)) {
                request.setProvider(sanitizedProps.get(PROVIDER_KEY));
            }
            if(sanitizedProps.containsKey(CONTENT_KEY)) {
                request.withContent(sanitizedProps.get(CONTENT_KEY).split(SYMBOL_DELIMITER));
            }
            if(sanitizedProps.containsKey(TYPE_KEY)) {
                request.setType(Type.valueOf(sanitizedProps.get(TYPE_KEY).toUpperCase()));
            }
            if(sanitizedProps.containsKey(DATE_KEY)) {
                request.setDate(DateUtils.stringToDate(sanitizedProps.get(DATE_KEY)));
            }
            if(sanitizedProps.containsKey(EXCHANGE_KEY)) {
                request.setExchange(sanitizedProps.get(EXCHANGE_KEY));
            }
            validate(request);
            return request;
        } catch (MarketDataRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new MarketDataRequestException(e,
                                                 INVALID_REQUEST);
        }
    }
    /**
     * Validates a <code>MarketDataRequest</code>.
     * 
     * <p>This method is intended to validate a request when it is believed to be complete
     * and ready to be submitted.  Some validation is performed that is relevant only to
     * a completed request.
     *
     * @param inRequest a <code>MarketDataRequest</code> value to validate
     * @throws MarketDataRequestException if the request is invalid
     */
    public static void validate(MarketDataRequest inRequest)
        throws MarketDataRequestException
    {
        if(inRequest == null) {
            throw new MarketDataRequestException(INVALID_REQUEST);
        }
        validateType(inRequest,
                     inRequest.type);
        validateDate(inRequest,
                     inRequest.date);
        validateExchange(inRequest,
                         inRequest.exchange);
        validateSymbols(inRequest,
                        inRequest.symbols.toArray(new String[inRequest.symbols.size()]));
        validateProvider(inRequest,
                         inRequest.provider);
        validateContent(inRequest,
                        inRequest.content);
        // this condition means that a date was provided but is not needed
        if(inRequest.content.contains(OHLC) &&
           inRequest.date == null) {
            // content is OHLC but no date
            throw new MarketDataRequestException(OHLC_NO_DATE);
        }
        if(!inRequest.content.contains(OHLC) &&
           inRequest.date != null) {
            // date specified but request not OHLC
            EXTRA_DATE.warn(MarketDataRequest.class);
        }
    }
    /**
     * Creates a new market data request.
     * 
     * <p>Attributes with default values will have those values set in the
     * returned market data request.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest newRequest()
    {
        return new MarketDataRequest();
    }
    /**
     * Create a new MarketDataRequest instance.
     *
     */
    public MarketDataRequest()
    {
        content.add(TOP_OF_BOOK);
    }
    /**
     * Adds the given symbols to the market data request. 
     *
     * <p>The given symbols must be non-null and non-empty.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inSymbols a <code>String[]</code> value containing symbols to add to the request
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified symbols result in an invalid request 
     */
    public MarketDataRequest withSymbols(String... inSymbols)
        throws MarketDataRequestException
    {
        setSymbols(inSymbols);
        return this;
    }
    /**
     * Adds the given symbols to the market data request. 
     *
     * <p>The given symbols must be non-null and non-empty.  The symbols may be a single symbol
     * or a series of symbols delimited by {@link #SYMBOL_DELIMITER}.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inSymbols a <code>String[]</code> value containing symbols to add to the request
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified symbols result in an invalid request 
     */
    public MarketDataRequest withSymbols(String inSymbols)
        throws MarketDataRequestException
    {
        if(isEmptyStringList(inSymbols)) {
            throw new MarketDataRequestException(MISSING_SYMBOLS);
        }
        setSymbols(inSymbols.split(SYMBOL_DELIMITER));
        return this;
    }
    /**
     * Adds the given provider to the market data request.
     *
     * <p>The provider is not validated because the set of valid providers is
     * resolved at run-time.  The specified provider must be non-null and of non-zero
     * length.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inProvider a <code>String</code> value containing the provider from which to request data
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified provider results in an invalid request 
     */
    public MarketDataRequest fromProvider(String inProvider)
        throws MarketDataRequestException
    {
        setProvider(inProvider);
        return this;
    }
    /**
     * Adds the given exchange to the market data request.
     *
     * <p>The exchange is not validated as the set of valid exchanges is dependent on the
     * provider and the provisioning within the domain of the services provided therein.
     * 
     * <p>This attribute is optional and no default is provided. 
     *
     * @param inExchange a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest fromExchange(String inExchange)
    {
        setExchange(inExchange);
        return this;
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given value must not be null or of zero-length and must correspond to
     * one or more valid {@link Content} values separated by {@link #SYMBOL_DELIMITER}.
     * Case is not considered.
     * 
     * <p>This attribute is required and no default is provided.
     *
     * @param inContent a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified content results in an invalid request 
     */
    public MarketDataRequest withContent(String inContent)
        throws MarketDataRequestException
    {
        if(isEmptyStringList(inContent)) {
            throw new MarketDataRequestException(MISSING_CONTENT);
        }
        try {
            return withContent(inContent.split(SYMBOL_DELIMITER));
        } catch (Exception e) {
            throw new MarketDataRequestException(e,
                                                 new I18NBoundMessage1P(INVALID_CONTENT,
                                                                        inContent));
        }
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given content value must not be null.  This attribute is required and no
     * default is provided.
     *
     * @param inContent a <code>Content[]</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified content results in an invalid request 
     */
    public MarketDataRequest withContent(Content...inContent)
        throws MarketDataRequestException
    {
        setContent(inContent);
        return this;
    }
    /**
     * Adds the given content to the market data request.
     *
     * <p>The given content value must not be null.  This attribute is required and no
     * default is provided.
     *
     * @param inContent a <code>String[]</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified content results in an invalid request 
     */
    public MarketDataRequest withContent(String...inContent)
        throws MarketDataRequestException
    {
        if(isEmptyStringList(inContent)) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                        inContent));
        }
        List<Content> newContents = new ArrayList<Content>();
        for(String contentString : inContent) {
            try {
                newContents.add(Content.valueOf(contentString.toUpperCase()));
            } catch (Exception e) {
                throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                            inContent));
            }
        }
        setContent(newContents.toArray(new Content[newContents.size()]));
        return this;
    }
    /**
     * Adds the given type to the market data request. 
     *
     * <p>The given value must not be null or of zero-length and must correspond to
     * a valid {@link Type}.  Case is not considered.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inType a <code>Type</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified type results in an invalid request
     */
    public MarketDataRequest ofType(String inType)
        throws MarketDataRequestException
    {
        try {
            return ofType(Type.valueOf(inType.toUpperCase()));
        } catch (Exception e) {
            throw new MarketDataRequestException(e,
                                                 new I18NBoundMessage1P(INVALID_TYPE,
                                                                        inType));
        }
    }
    /**
     * Adds the given type to the market data request. 
     *
     * <p>The given value must not be null.  This attribute is required and no default is provided.
     * 
     * @param inType a <code>Type</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified type results in an invalid request
     */
    public MarketDataRequest ofType(Type inType)
        throws MarketDataRequestException
    {
        setType(inType);
        return this;
    }
    /**
     * Adds the given date to the market data request. 
     *
     * <p>The given value must be greater than 0 and is interpreted as the number
     * of milliseconds since EPOCH.  The date is valid (and required) only for
     * requests with <code>Content</code> of {@link Content#OHLC}, but is
     * not otherwise forbidden.  If specified for other <code>Content</code>
     * types, the attribute is ignored.
     * 
     * <p>This attribute is required for requests of content {@link Content#OHLC} and
     * no default is provided.
     *
     * @param inDate a <code>long</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified date results in an invalid request 
     */ 
    public MarketDataRequest asOf(long inDate)
        throws MarketDataRequestException
    {
        if(inDate < 0) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_DATE,
                                                                        inDate));
        }
        return asOf(new Date(inDate));
    }
    /**
     * Adds the given date to the market data request. 
     *
     * <p>The given value must be parseable according to the rules described
     * in {@link DateUtils#stringToDate(String)}.
     *  
     * <p>The date is valid (and required) only for
     * requests with <code>Content</code> of {@link Content#OHLC}, but is
     * not otherwise forbidden.  If specified for other <code>Content</code>
     * types, the attribute is ignored.
     * 
     * <p>This attribute is required for requests of content {@link Content#OHLC} and
     * no default is provided.
     *
     * @param inDate a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified date results in an invalid request 
     */ 
    public MarketDataRequest asOf(String inDate)
        throws MarketDataRequestException
    {
        return asOf(DateUtils.stringToDate(inDate));
    }
    /**
     * Adds the given date to the market data request. 
     *
     * <p>The given value must not be null.  The date is valid (and required) only for
     * requests with <code>Content</code> of {@link Content#OHLC}, but is
     * not otherwise forbidden.  If specified for other <code>Content</code>
     * types, the attribute is ignored.
     * 
     * <p>This attribute is required for requests of content {@link Content#OHLC} and
     * no default is provided.
     *
     * @param inDate a <code>Date</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified date results in an invalid request 
     */
    public MarketDataRequest asOf(Date inDate)
        throws MarketDataRequestException
    {
        setDate(inDate);
        return this;
    }
    /**
     * Get the symbols value.
     * 
     * @return a <code>String[]</code> value
     */
    public String[] getSymbols()
    {
        return symbols.toArray(new String[symbols.size()]);
    }
    /**
     * Get the provider value.
     *
     * @return a <code>String</code> value
     */
    public String getProvider()
    {
        if(provider == null ||
           provider.isEmpty()) {
            return null;
        }
        return provider;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        if(exchange == null ||
           exchange.isEmpty()) {
            return null;
        }
        return exchange;
    }
    /**
     * Get the content value.
     * 
     * @return a <code>Set&lt;Content&gt;</code> value
     */
    public Set<Content> getContent()
    {
        return Collections.unmodifiableSet(content);
    }
    /**
     * Determines if the request is valid apropos the given capabilities.
     *
     * @param inCapabilities a <code>Content[]</code> value containing the capabilities against which to verify the request
     * @return a <code>boolean</code> value indicating whether the request if valid according to the given capabilities
     */
    public boolean validateWithCapabilities(Content...inCapabilities)
    {
        Set<Content> results = new HashSet<Content>(content);
        results.removeAll(Arrays.asList(inCapabilities));
        return results.isEmpty();
    }
    /**
     * Get the date value.
     * 
     * @return a <code>Date</code> value
     */
    public Date getDate()
    {
        if(date == null) {
            return null;
        }
        return date;
    }
    /**
     * Get the type value.
     * 
     * @return a <code>Type</code> value
     */
    public Type getType()
    {
        return type;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        result = prime * result + ((symbols == null) ? 0 : symbols.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketDataRequest other = (MarketDataRequest) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (provider == null) {
            if (other.provider != null)
                return false;
        } else if (!provider.equals(other.provider))
            return false;
        if (symbols == null) {
            if (other.symbols != null)
                return false;
        } else if (!symbols.equals(other.symbols))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
    /**
     * Verifies that the given symbols are valid.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inSymbols a <code>String[]</code> value
     * @throws MarketDataRequestException if the symbols are not valid
     */
    private static void validateSymbols(MarketDataRequest inRequest,
                                        String[] inSymbols)
        throws MarketDataRequestException
    {
        if(isEmptyStringList(inSymbols)) {
            throw new MarketDataRequestException(MISSING_SYMBOLS);
        }
        for(String symbol : inSymbols) {
            if(symbol == null ||
               symbol.trim().isEmpty()) {
                throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_SYMBOLS,
                                                                            Arrays.toString(inSymbols)));
            }
        }
    }
    /**
     * Verifies that the given <code>Type</code> is valid.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inType a <code>Type</code> value
     * @throws MarketDataRequestException if the given <code>Type</code> is not valid
     */
    private static void validateType(MarketDataRequest inRequest,
                                     Type inType)
        throws MarketDataRequestException
    {
        if(inType == null) {
            throw new MarketDataRequestException(MISSING_TYPE);
        }
    }
    /**
     * Verifies that the given <code>Date</code> is valid. 
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inDate a <code>Date</code> value
     * @throws MarketDataRequestException if the given <code>Date</code> is not valid
     */
    private static void validateDate(MarketDataRequest inRequest,
                                     Date inDate)
        throws MarketDataRequestException
    {
        // nothing to do
    }
    /**
     * Verifies that the given <code>Exchange</code> is valid.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inExchange a <code>String</code> value
     */
    private static void validateExchange(MarketDataRequest inRequest,
                                         String inExchange)
    {
        // nothing to do
    }
    /**
     * Verifies that the provider is valid on the given <code>MarketDataRequest</code>.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inProvider a <code>String</code> value
     * @throws MarketDataRequestException if the <code>Provider</code> is not valid
     */
    private static void validateProvider(MarketDataRequest inRequest,
                                         String inProvider)
        throws MarketDataRequestException
    {
        if(inProvider == null ||
           inProvider.isEmpty()) {
            throw new MarketDataRequestException(MISSING_PROVIDER);
        }
    }
    /**
     * Verifies that the <code>Content</code> on the given <code>MarketDataRequest</code> is valid.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inContent a <code>Content[]</code> value
     * @throws MarketDataRequestException if the <code>Content</code> is not valid
     */
    private static void validateContent(MarketDataRequest inRequest,
                                        Content...inContent)
        throws MarketDataRequestException
    {
        if(inContent == null ||
           inContent.length == 0) {
            throw new MarketDataRequestException(MISSING_CONTENT);
        }
        if(!isValidEnumList(inContent)) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                        Arrays.toString(inContent)));
        }
    }
    /**
     * Verifies that the <code>Content</code> on the given <code>MarketDataRequest</code> is valid.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inContent a <code>Set&lt;Content&gt;</code> value
     * @throws MarketDataRequestException if the <code>Content</code> is not valid
     */
    private static void validateContent(MarketDataRequest inRequest,
                                        Set<Content> inContent)
        throws MarketDataRequestException
    {
        if(inContent == null ||
           inContent.isEmpty()) {
            throw new MarketDataRequestException(MISSING_CONTENT);
        }
        if(!isValidEnumList(inContent.toArray(new Content[inContent.size()]))) {
            throw new MarketDataRequestException(new I18NBoundMessage1P(INVALID_CONTENT,
                                                                        String.valueOf(inContent)));
        }
    }
    /**
     * Checks to see if the given <code>String</code> represents an empty list.
     * 
     * <p>The list is considered empty if it is empty or if all strings in the list are whitespace or empty.
     *
     * @param inStrings a <code>String</code> value allegedly containing a list of tokens delimited by {@link MarketDataRequest#SYMBOL_DELIMITER}
     * @return a <code>boolean</code>value
     */
    private static boolean isEmptyStringList(String inStrings)
    {
        if(inStrings == null ||
           inStrings.isEmpty()) {
            return true;
        }
        return isEmptyStringList(inStrings.split(MarketDataRequest.SYMBOL_DELIMITER));
    }
    /**
     * Checks to see if the given <code>String[]</code> value represents an empty list.
     * 
     * <p>The list is considered empty if the array is empty or contains only null or whitespace values.
     *
     * @param inStrings a <code>String[]</code> value
     * @return a <code>boolean</cod> value
     */
    private static boolean isEmptyStringList(String[] inStrings)
    {
        if(inStrings == null ||
           inStrings.length == 0) {
            return true;
        }
        for(String string : inStrings) {
            if(string != null &&
               !string.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    /**
     * Checks to see if the given <code>Enum&lt;E&gt;[]</code> value represents a list of valid enums.
     *
     * @param inEnums an <code>Enum&lt;E&gt;[]</code> value
     * @return a <code>boolean</code> value
     */
    private static <E extends Enum<E>> boolean isValidEnumList(Enum<E>[] inEnums)
    {
        if(inEnums == null ||
           inEnums.length == 0) {
            return false;
        }
        for(Enum<E> e : inEnums) {
            if(e == null) {
                return false;
            }
        }
        return true;
    }
    /**
     * Sets the type value.
     *
     * <p>The given value must not be null.  This attribute is required and no default is provided.
     * 
     * @param a <code>Type</code> value
     * @throws MarketDataRequestException if the specified type results in an invalid request
     */
    private void setType(Type inType)
        throws MarketDataRequestException
    {
        validateType(this,
                     inType);
        type = Type.valueOf(inType.toString());
    }
    /**
     * Sets the date value.
     *
     * <p>The date is valid (and required) only for
     * requests with <code>Content</code> of {@link Content#OHLC}, but is
     * not otherwise forbidden.  If specified for other <code>Content</code>
     * types, the attribute is ignored.
     * 
     * @param a <code>Date</code> value
     * @throws MarketDataRequestException if the specified date results in an invalid request 
     */
    private void setDate(Date inDate)
        throws MarketDataRequestException
    {
        validateDate(this,
                     inDate);
        if(inDate == null) {
            date = null;
        } else {
            date = new Date(inDate.getTime());
        }
    }
    /**
     * Sets the symbols.
     *
     * <p>The given symbols must be non-null and non-empty.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inSymbols a <code>String[]</code> value containing symbols to add to the request
     * @return a <code>MarketDataRequest</code> value
     * @throws MarketDataRequestException if the specified symbols result in an invalid request 
     */
    private void setSymbols(String[] inSymbols)
        throws MarketDataRequestException
    {
        validateSymbols(this,
                        inSymbols);
        // synchronize to make the symbol change atomic
        synchronized(this) {
            symbols.clear();
            for(String symbol:inSymbols) {
                symbols.add(symbol.trim());
            }
        }
    }
    /**
     * Sets the exchange.
     *
     * <p>The exchange is not validated as the set of valid exchanges is dependent on the
     * provider and the provisioning within the domain of the services provided therein.
     * 
     * <p>This attribute is optional and no default is provided. 
     *
     * @param inExchange a <code>String</code> value
     */
    private void setExchange(String inExchange)
    {
        validateExchange(this,
                         inExchange);
        if(inExchange == null ||
           inExchange.isEmpty()) {
            exchange = null;
        } else {
            exchange = new String(inExchange);
        }
    }
    /**
     * Sets the provider.
     *
     * <p>The provider is not validated because the set of valid providers is
     * resolved at run-time.  The specified provider must be non-null and of non-zero
     * length.
     * 
     * <p>This attribute is required and no default is provided.
     * 
     * @param inProvider a <code>String</code> value containing the provider from which to request data
     * @throws MarketDataRequestException if the specified provider results in an invalid request 
     */
    private void setProvider(String inProvider)
        throws MarketDataRequestException
    {
        validateProvider(this,
                         inProvider);
        provider = new String(inProvider);
    }
    /**
     * Sets the content value.
     *
     * <p>This attribute is required.  If omitted, the value will be {@link Content#TOP_OF_BOOK}.
     * 
     * @param a <code>Content[]</code> value
     * @throws MarketDataRequestException if the given content value is invalid 
     */
    private void setContent(Content...inContent)
        throws MarketDataRequestException
    {
        validateContent(this,
                        inContent);
        content.clear();
        content.addAll(Arrays.asList(inContent));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        boolean delimiterNeeded = false;
        if(symbols != null &&
           !symbols.isEmpty()) {
            output.append(SYMBOLS_KEY).append(KEY_VALUE_SEPARATOR).append(symbols.toString().replaceAll("[\\[\\]]", //$NON-NLS-1$
                                                                                                        "")); //$NON-NLS-1$
            delimiterNeeded = true;
        }
        if(provider != null &&
           !provider.isEmpty()) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            }
            output.append(PROVIDER_KEY).append(KEY_VALUE_SEPARATOR).append(String.valueOf(provider));
            delimiterNeeded = true;
        }
        if(!content.isEmpty()) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            }
            output.append(CONTENT_KEY).append(KEY_VALUE_SEPARATOR).append(content.toString().replaceAll("[\\[\\] ]", //$NON-NLS-1$
                                                                                                        "")); //$NON-NLS-1$
            delimiterNeeded = true;
        }
        if(type != null) {
            if(delimiterNeeded) {
               output.append(KEY_VALUE_DELIMITER);
            }
            output.append(TYPE_KEY).append(KEY_VALUE_SEPARATOR).append(type);
            delimiterNeeded = true;
        }
        if(exchange != null &&
           !exchange.isEmpty()) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            }
            output.append(EXCHANGE_KEY).append(KEY_VALUE_SEPARATOR).append(String.valueOf(exchange));
            delimiterNeeded = true;
        }
        if(date != null) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            }
            output.append(DATE_KEY).append(KEY_VALUE_SEPARATOR).append(DateUtils.dateToString(date));
            delimiterNeeded = true;
        }
        return output.toString();
    }
    /**
     * the symbols for which to request data
     */
    private final List<String> symbols = new ArrayList<String>();
    /**
     * the provider key from which to request data
     */
    private String provider;
    /**
     * the exchange from which to request data
     */
    private String exchange;
    /**
     * the request content
     */
    private final Set<Content> content = new HashSet<Content>();
    /**
     * the date as of which to request data
     */
    private Date date;
    /**
     * the request type
     */
    private Type type = SUBSCRIPTION;
    /**
     * The content types for market data requests.
     * 
     * <p>In this context, <em>content</em> refers to the type of market data request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static enum Content
    {
        /**
         * best-bid-and-offer only
         */
        TOP_OF_BOOK(1),
        /**
         * NYSE OpenBook data
         */
        OPEN_BOOK(0),
        /**
         * Open-High-Low-Close data
         */
        OHLC(1),
        /**
         * NASDAQ TotalView data
         */
        TOTAL_VIEW(0),
        /**
         * NASDAQ Level II data
         */
        LEVEL_2(0),
        /**
         * latest trade
         */
        LATEST_TICK(1);
        /**
         * Gets the depth implied by the content type.
         *
         * @return an <code>int</code> value
         */
        public int getDepth()
        {
            return impliedDepth;
        }
        /**
         * Create a new Content instance.
         *
         * @param inImpliedDepth an <code>int</code> value
         */
        private Content(int inImpliedDepth)
        {
            impliedDepth = inImpliedDepth;
        }
        /**
         * depth implied by the type of request
         */
        private final int impliedDepth;
    }
    /**
     * The request types for market data requests.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static enum Type
    {
        /**
         * request for a single data-point with no updates
         */
        SNAPSHOT,
        /**
         * request for a stream of updates as they become available until cancelled
         */
        SUBSCRIPTION
    }
    private static final long serialVersionUID = 1L;
}