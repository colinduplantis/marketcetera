package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;
import org.marketcetera.core.ClassVersion;

/**
 * A registry of {@link Closeable} instances.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class CloseableRegistry
    implements Closeable
{

    // INSTANCE DATA.

    private LinkedList<Closeable> mRegistry=new LinkedList<Closeable>();


	// INSTANCE METHODS.

    /**
     * Registers a {@link Closeable} instance with the receiver. An
     * instance (dependent) that depends upon another instance
     * (parent) must be registered <i>after</i> the parent.
     *
     * @param closeable The closeable.
     */
    
    public void register
        (Closeable closeable)
    {
        mRegistry.addFirst(closeable);
    }

    /**
     * Closes all {@link Closeable} instances registered with the
     * receiver. <i>All</i> instances are always closed, even if an
     * error occurs while closing an instance; such errors are merely
     * logged, but do not prevent further closures. The order of
     * closures is the reverse of the registration order.
     */

    @Override
    public void close()
    {
        for (Closeable closeable:mRegistry) {
            try {
                closeable.close();
            } catch (IOException ex) {
                Messages.LOGGER.error(this,ex,Messages.CLOSING_FAILED);
            }
        }
    }
}
