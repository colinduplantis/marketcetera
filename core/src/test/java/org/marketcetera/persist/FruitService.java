package org.marketcetera.persist;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FruitService
        extends TestEntityService<Fruit>
{
    public Fruit add(String inName,
                     String inDescription,
                     Fruit.Type inType);
}
