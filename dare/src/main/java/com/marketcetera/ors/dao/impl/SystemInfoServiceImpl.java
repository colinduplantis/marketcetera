package com.marketcetera.ors.dao.impl;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.marketcetera.ors.dao.DatabaseVersion;
import com.marketcetera.ors.dao.DatabaseVersionInitializer;
import com.marketcetera.ors.dao.DatabaseVersionMismatch;
import com.marketcetera.ors.dao.PersistentSystemInfo;
import com.marketcetera.ors.dao.SystemInfoDao;
import com.marketcetera.ors.dao.SystemInfoService;

/* $License$ */

/**
 * Provides access to {@link PersistentSystemInfo} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemInfoServiceImpl.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id: SystemInfoServiceImpl.java 17266 2017-04-28 14:58:00Z colin $")
public class SystemInfoServiceImpl
        implements SystemInfoService
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#getDatabaseVersion()
     */
    @Override
    public DatabaseVersion getDatabaseVersion()
    {
        PersistentSystemInfo databaseVersion = systemInfoDao.findByName(SystemInfoService.DATABASE_VERSION);
        if(databaseVersion == null) {
            return DatabaseVersion.NO_VERSION;
        } else {
            return new DatabaseVersion(databaseVersion);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#verifyDatabaseVersion()
     */
    @Override
    public void verifyDatabaseVersion()
    {
        DatabaseVersion actualVersion = getDatabaseVersion();
        if(!DatabaseVersion.CURRENT_VERSION.equals(actualVersion)) {
            throw new DatabaseVersionMismatch(actualVersion);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#initializeDatabaseVersion()
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void initializeDatabaseVersion()
    {
        PersistentSystemInfo databaseVersion = systemInfoDao.findByName(SystemInfoService.DATABASE_VERSION);
        if(databaseVersion != null) {
            SLF4JLoggerProxy.info(this,
                                  "Database already initialized");
            return;
        }
        databaseVersion = new PersistentSystemInfo();
        databaseVersion.setName(SystemInfoService.DATABASE_VERSION);
        databaseVersion.setDescription("indicates current database schema version");
        databaseVersion.setValue(DatabaseVersion.CURRENT_VERSION.getVersion());
        systemInfoDao.save(databaseVersion);
    }
    /**
     * Get the systemInfoDao value.
     *
     * @return a <code>SystemInfoDao</code> value
     */
    public SystemInfoDao getSystemInfoDao()
    {
        return systemInfoDao;
    }
    /**
     * Sets the systemInfoDao value.
     *
     * @param inSystemInfoDao a <code>SystemInfoDao</code> value
     */
    public void setSystemInfoDao(SystemInfoDao inSystemInfoDao)
    {
        systemInfoDao = inSystemInfoDao;
    }
    /**
     * provides datastore access to system info objects
     */
    @Autowired
    private SystemInfoDao systemInfoDao;
    /**
     * initializes the database system info, if supplied
     */
    @SuppressWarnings("unused")
    @Autowired(required=false)
    private DatabaseVersionInitializer databaseVersionInitiator;
}
