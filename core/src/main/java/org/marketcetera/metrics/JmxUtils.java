package org.marketcetera.metrics;

import javax.management.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Class for exporting {@link ThreadedMetric} management interface
 * {@link ThreadedMetricMXBean} on an {@link javax.management.MBeanServer}.
 * <p>
 * The management interface is automatically configured if the
 * {@link Configurator} has been used to configure the metrics and the value
 * of the property {@link #METC_METRICS_JMX_ENABLE metc.metrics.jmx.enable}
 * is set to true.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 * @see ThreadedMetricMXBean
 */
@ClassVersion("$Id$")
public class JmxUtils {
    /**
     * Registers the {@link ThreadedMetric} management interface with
     * the supplied bean server.
     *
     * @param inServer the mbean server to register the management interface to.
     * @throws MBeanRegistrationException if a problem occurs registering the interface
     * @throws InstanceAlreadyExistsException if the interface already exists
     * @throws NotCompliantMBeanException if the there is a problem with the bean
     *
     * @see MBeanServer#registerMBean(Object, javax.management.ObjectName)
     */
    public static void registerMgmtInterface(MBeanServer inServer)
            throws MBeanRegistrationException, InstanceAlreadyExistsException,
            NotCompliantMBeanException {
        inServer.registerMBean(new ThreadedMetricBeanImpl(), DEFAULT_NAME);
        Messages.LOG_REGISTERED_MXBEAN.info(JmxUtils.class, DEFAULT_NAME);
    }
    /**
     * Unregisters the {@link ThreadedMetric} management interface from the
     * supplied bean server.
     *
     * @param inServer the mbean server to unregister the management interface
     * from.
     * @throws InstanceNotFoundException if the instance cannot be found
     * @throws MBeanRegistrationException if a problem occurs unregistering the interface
     *
     * @see MBeanServer#unregisterMBean(javax.management.ObjectName) 
     */
    public static void unregisterMgmtInterface(MBeanServer inServer)
            throws InstanceNotFoundException, MBeanRegistrationException {
        inServer.unregisterMBean(DEFAULT_NAME);
        Messages.LOG_UNREGISTERED_MXBEAN.info(JmxUtils.class, DEFAULT_NAME);
    }
    /**
     * The object name that is used to register the {@link ThreadedMetricMXBean}.
     * The value of the object name should
     * <code>org.marketcetera.metrics:name=ThreadedMetric</code>.
     */
    public static final ObjectName DEFAULT_NAME;
    static {
        try {
            DEFAULT_NAME = new ObjectName(
                    ThreadedMetric.class.getPackage().getName(),
                    "name",  //$NON-NLS-1$
                    ThreadedMetric.class.getSimpleName());
        } catch (MalformedObjectNameException e) {
            //should not happen. if it does makes sure that it results in
            //unit test failure.
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Utility class. Cannot be instantiated.
     */
    private JmxUtils() {
    }

    /**
     * The {@link Configurator} property that can be used to enable JMX
     * interface for the Metrics on initialization.
     */
    static final String METC_METRICS_JMX_ENABLE =
            "metc.metrics.jmx.enable";  //$NON-NLS-1$
}
