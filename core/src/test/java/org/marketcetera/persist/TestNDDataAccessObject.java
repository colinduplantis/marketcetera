package org.marketcetera.persist;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TestNDDataAccessObject<Clazz extends NDEntityBase>
        extends TestDataAccessObject<Clazz>
{
    public void setExceptionBefore(RuntimeException inException);
    public void setExceptionAfter(RuntimeException inException);
    public void reset();
}
