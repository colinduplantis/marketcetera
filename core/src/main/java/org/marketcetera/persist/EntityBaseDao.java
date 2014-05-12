package org.marketcetera.persist;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@NoRepositoryBean
public interface EntityBaseDao<Clazz extends EntityBase>
        extends PagingAndSortingRepository<Clazz,Long>
{
}
