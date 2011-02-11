/*
   Copyright 2006 Eros Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.blarty.eros.logging;


import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;

import org.blarty.eros.ErosLogger;

/**
 * Implementation of a <code>Logger</code> configured to automatically log
 * to a number of outputs.  This logger will forward logs to an Eros service,
 * the console and rolling files.
 * <p>
 * The ErosHandler added to this logger uses a number of system
 * properties when instantiated to control output.
 * @see ErosHandler
 */
public class ErosLoggerImpl extends Logger {

    private static Logger erosLoggerImpl = null;

    private ErosLoggerImpl(ErosLogger erosLogger, String appName) throws Exception{
        super(ErosLogger.EROS_LOGGER_NAME, null);
        this.setUseParentHandlers(false);
        this.addHandler(new ErosHandlerImpl(erosLogger, appName));
    }

    /**
     * Overidden to ensure changes to the Level for this logger are forwarded
     * to the ErosHandler.
     * @param newLevel The new logging level.
     * @throws SecurityException
     * @see Logger#setLevel(Level)
     */
    public void setLevel(Level newLevel) throws SecurityException {
        super.setLevel(newLevel);
        Handler[] handlers = this.getHandlers();
        for (int i = 0; i < handlers.length; i++) {
            Handler handler = handlers[i];
            if(handler instanceof ErosHandler) {
                handler.setLevel(newLevel);
            }
        }
    }

    /**
     * Provides a pre-configured logger that will forward requests to an Eros
     * service, the console and rolling files.
     * @param erosLogger The eros service reference to forward logs to.
     * @param appName The name of the application that will be logging.
     * @return A pre-configured logger ready for use with all handlers added.
     * @throws Exception Any problems creating the logger and adding the handlers.
     */
    public static Logger getLogger(ErosLogger erosLogger, String appName) throws Exception{
        if(erosLoggerImpl == null) {
            erosLoggerImpl = new ErosLoggerImpl(erosLogger, appName);
        }
        return erosLoggerImpl;
    }
}
