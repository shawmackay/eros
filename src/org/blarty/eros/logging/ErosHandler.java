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

/*
 * Created by IntelliJ IDEA.
 * User: Chrisl
 * Date: 03-Jul-02
 * Time: 14:05:03
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.blarty.eros.logging;

import java.util.logging.Level;
import java.io.File;
import java.io.IOException;

/**
 * Provides logging with Eros through the standard
 * <code>java.util.logging</code> package introduced at JDK1.4.
 * <p>
 * Uses the <code>ConsoleHandler</code> to also provide an output of the
 * log to the console.
 * </p>
 * <p>
 * This hanlder also provides output to a file handler for each logging
 * call made.  The naming of the logging files is controlled by the handler
 * and incorporates the application name supplied at construction.
 * The output directory for these files can be specified in two different
 * ways.  Upon construction of the Hanlder, the system property
 * <i>org.blarty.eros.logdir</i> is checked.  If this property is set an
 * attempt is made to write files to this directory.
 * </p>
 * <p>
 * The directory can also
 * be set using the <code>setOutputDir(File loggingDir)</code> method.  This
 * will override the system property directory if set.
 * </p>
 * <p>
 * The default destination for the file is the value of the system property
 * <i>user.home</i>, this is controlled by the utilised class
 * <code>java.util.logging.FileHanlder</code> and is therefore subject to
 * change.
 * </p>
 * Requires java version 1.4.0 or greater.
 */
public interface ErosHandler {

    /**
     * Sets the level for handler, this is the same as calling
     * setConsoleLevel(level)
     * setFileLevel(level)
     * @param level The level for the handler
     * @see #setConsoleLevel(Level level)
     * @see #setFileLevel(Level level)
     */
    void setLevel(Level level);

    /**
     * Sets the level for the console output of the handler.  All logs that
     * are of value level or above will be directed to the console output.
     * @param level The level for the console output.
     */
    void setConsoleLevel(Level level);

    /**
     * Allows the file output to be redirected to a different directory.
     * The default directory is determined from the system propertey
     * <code>user.home</code>.
     * <p>
     * The name of the file cannot be altered, only the directory which it
     * is stored to.  The filename is made up of the application name plus
     * the file version and has the extension <i>.log</i>.
     * @param loggingDir The existing directory to store the log files.
     * @throws IOException Problems accessing the directory specified.
     */
    void setOutputDir(File loggingDir) throws IOException;

    /**
     * Sets the level for the file output from this handler.
     * @param level The level for the file output.
     */
    void setFileLevel(Level level);
}
