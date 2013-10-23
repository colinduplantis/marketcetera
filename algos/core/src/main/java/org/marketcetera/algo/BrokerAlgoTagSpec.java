package org.marketcetera.algo;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.core.Validator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Describes a single tag of a broker algo.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class BrokerAlgoTagSpec
        implements Serializable, Comparable<BrokerAlgoTagSpec>
{
    /**
     * Get the tag value.
     *
     * @return an <code>int</code> value
     */
    public int getTag()
    {
        return tag;
    }
    /**
     * Sets the tag value.
     *
     * @param inTag an <code>int</code> value
     */
    public void setTag(int inTag)
    {
        tag = inTag;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = StringUtils.trimToNull(inDescription);
    }
    /**
     * Get the mandatory value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsMandatory()
    {
        return mandatory;
    }
    /**
     * Sets the mandatory value.
     *
     * @param inMandatory a <code>boolean</code> value
     */
    public void setIsMandatory(boolean inMandatory)
    {
        mandatory = inMandatory;
    }
    /**
     * Get the pattern value.
     *
     * @return a <code>String</code> value
     */
    public String getPattern()
    {
        return pattern;
    }
    /**
     * Sets the pattern value.
     *
     * @param inPattern a <code>String</code> value
     */
    public void setPattern(String inPattern)
    {
        pattern = StringUtils.trimToNull(inPattern);
        if(pattern != null) {
            // validate the pattern by compiling it (we don't actually need the value now, so discard it)
            Pattern.compile(pattern);
        }
    }
    /**
     * Get the validator value.
     *
     * @return a <code>Validator&lt;BrokerAlgoTag&gt;</code> value
     */
    public Validator<BrokerAlgoTag> getValidator()
    {
        return validator;
    }
    /**
     * Sets the validator value.
     *
     * @param inValidator a <code>Validator&lt;BrokerAlgoTag&gt;</code> value
     */
    public void setValidator(Validator<BrokerAlgoTag> inValidator)
    {
        validator = inValidator;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BrokerAlgoTagSpec [tag=").append(tag).append(", description=").append(description) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", mandatory=").append(mandatory).append(", pattern=").append(pattern).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(tag).toHashCode();
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
        BrokerAlgoTagSpec other = (BrokerAlgoTagSpec) obj;
        return new EqualsBuilder().append(tag,other.tag).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BrokerAlgoTagSpec inOther)
    {
        return new Integer(tag).compareTo(inOther.tag);
    }
    /**
     * order tag for this broker algo
     */
    @XmlAttribute
    private int tag;
    /**
     * human-readable description of this algo tag
     */
    @XmlAttribute
    private String description;
    /**
     * indicates if this tag is mandatory or not
     */
    @XmlAttribute
    private boolean mandatory = false;
    /**
     * optional regular expression used to validate algo tag value
     */
    @XmlAttribute
    private String pattern;
    /**
     * optional validator used to validate algo tag value
     */
    private transient Validator<BrokerAlgoTag> validator;
    private static final long serialVersionUID = 3711535397610380635L;
}
