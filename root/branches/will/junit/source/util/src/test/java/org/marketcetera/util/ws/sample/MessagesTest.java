package org.marketcetera.util.ws.sample;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.l10n.MessageComparator;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesMatch()
        throws Exception
    {
        MessageComparator comparator=new MessageComparator
            (SampleMessages.class);
        assertTrue(comparator.getDifferences(),comparator.isMatch());

        comparator=new MessageComparator(SampleMessages.class,Locale.FRENCH);
        assertTrue(comparator.getDifferences(),comparator.isMatch());
    }
}
