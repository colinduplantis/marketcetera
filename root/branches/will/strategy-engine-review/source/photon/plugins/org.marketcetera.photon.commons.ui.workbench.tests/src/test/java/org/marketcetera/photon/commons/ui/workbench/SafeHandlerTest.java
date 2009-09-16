package org.marketcetera.photon.commons.ui.workbench;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;

/* $License$ */

/**
 * Tests {@link SafeHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SafeHandlerTest extends PhotonTestBase {

    @Test
    public void testExceptionReported() throws Exception {
        final RuntimeException exception = new RuntimeException("Hello World");
        final AbstractHandler fixture = new SafeHandler() {
            @Override
            protected void executeSafely(ExecutionEvent event)
                    throws ExecutionException {
                throw exception;
            }
        };
        new ExpectedFailure<ExecutionException>("Hello World") {
            @Override
            protected void run() throws Exception {
                try {
                    fixture.execute(new ExecutionEvent());
                } catch (ExecutionException e) {
                    assertThat(e.getCause(), is((Throwable) exception));
                    throw e;
                }
            }
        };
    }
}
