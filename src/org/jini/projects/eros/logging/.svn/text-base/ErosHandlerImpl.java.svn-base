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


/**
 * Title:        Eros Logging Service<p>
 * Description:  Project to provide distributed system logging.<p>
 * Copyright:    Copyright (c) C. Lunn<p>
 * Company:      Countrywide Assured<p>
 * @author C. Lunn
 * @version 1.0
 */
package org.jini.projects.eros.logging;

import java.util.logging.*;
import java.util.Date;
import java.text.MessageFormat;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;

import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.LogLevel;


/**
 * Implementation of the Handler for Eros.
 * The class uses a number of system properties when created.
 * <p>
 * <i>org.jini.projects.eros.logsize</i> - if specified controls the maximum size
 * of the logs files that are created.  This should be specified in KB.
 * If not specified the value of <code>DEFAULT_LOG_SIZE</code> will be used.
 * <p>
 * <i>org.jini.projects.eros.numlogs</i> - if specified controls the number of
 * generations of log files that will be maintained.  If not specified the
 * value of <code>DEFAULT_NUM_LOGS</code> will be used.
 *
 */
public class ErosHandlerImpl extends StreamHandler implements ErosHandler {

    /**
     * The default size of the log files generated - 100KB.
     */
    public static final int DEFAULT_LOG_SIZE = 100;
    /**
     * The default number of generations of log files kept - 3.
     */
    public static final int DEFAULT_NUM_LOGS = 3;
    private static final Level DEFAULT_LEVEL = Level.INFO;

    private boolean debug = Boolean.getBoolean("org.jini.projects.eros.debug");
    private ErosLogger logger = null;
    private String applicationName = null;
    private OutputFormatter formatter = new OutputFormatter();
    private FileHandler fileOutput = null;
    private Level consoleLevel = Level.INFO;
    private Level fileLevel = Level.INFO;
    private Level publishLevel = Level.WARNING;
    private String outputFile = "";

    /**
     * Creates the Handler with the Eros logger to use, with the option to customise the level above which Eros will log to the central store;
     * @param logger The Eros logger object which all logs will be forwarded to.
     * @param appName The name of the application that is requesting this logger.
     * @param publishLevel The minimum logging level that will make a log record be published to the central store.  
     * @throws Exception Any problems initialising the logger.
     */
    public ErosHandlerImpl(ErosLogger logger, String appName, Level publishLevel) throws Exception {
            this(logger, appName);
            this.publishLevel = publishLevel;
    }
    
    
    /**
     * Creates the Handler with the Eros logger to use.
     * @param logger The Eros logger object which all logs will be forwarded to.
     * @param appName The name of the application that is requesting this logger.
     * @throws Exception Any problems initialising the logger.
     */
    public ErosHandlerImpl(ErosLogger logger, String appName) throws Exception {
        super();
        this.logger = logger;
        this.applicationName = appName;
       
        setFormatter(formatter);
        setOutputStream(System.err);

        String logDir = System.getProperty("org.jini.projects.eros.logdir");
        if( logDir != null ) {
            setOutputDir(new File(logDir));
        } else {
                File dir = new File("logs");
                if(!dir.exists())
                        dir.mkdirs();
            outputFile = "logs/" + applicationName + "%g.log";
        }

        setLevel(DEFAULT_LEVEL);
    }

    //Inherit comments
    public void setOutputDir(File loggingDir) throws IOException {
        if( loggingDir.isDirectory() ) {
            outputFile = loggingDir.getAbsolutePath() + File.separator +
                            applicationName + "%g.log";
            createFileHandler(outputFile);
        } else {
            throw new IOException("Unable to write log file to " +
                    "directory specified - " + loggingDir.getAbsolutePath());
        }
    }

    /**
     * Creates the file handler that will be used.
     * @param outFile The path for the file.
     * @throws IOException Problems creating the file.
     */
    private void createFileHandler(String outFile) throws IOException {
        Integer logSize = Integer.getInteger("org.jini.projects.eros.logsize");
        if(logSize == null) {
            logSize = new Integer(DEFAULT_LOG_SIZE);
        }

        Integer logs = Integer.getInteger("org.jini.projects.eros.numlogs");
        if(logs == null) {
            logs = new Integer(DEFAULT_NUM_LOGS);
        }

        fileOutput = new FileHandler(outFile, logSize.intValue() * 1000,
                                        logs.intValue(), false);
        fileOutput.setFormatter(formatter);
        fileOutput.setLevel(fileLevel);
    }

    //Inherit comments
    public Level getLevel() {
        return Level.ALL;
    }

    //Inherit comments
    public void setLevel(Level level) {
        if(debug) System.out.println("EROS - Setting level to " +
                                        level.getName());
        setConsoleLevel(level);
        setFileLevel(level);
    }

    //Inherit comments
    public void setConsoleLevel(Level level) {
        if(debug) System.out.println("EROS - Setting Console level to " +
                                        level.getName());
        this.consoleLevel = level;
    }

    //Inherit comments
    public void setFileLevel(Level level) {
        fileLevel = level;
        if( fileOutput != null ) {
            if(debug) System.out.println("EROS - Setting File level to " +
                                            level.getName());
            fileOutput.setLevel(level);
        }
    }

    /**
     * Determines if the Handler will publish the LogRecord.
     * This always returns true as this handler may have different levels
     * for its output destinations.
     * @param logRec The LogRecord which should be published.
     * @return Always returns true.
     */
    public boolean isLoggable(LogRecord logRec) {
        return true;
    }

    /**
     * Publishes the record supplied.
     * The data is extracted from the LogRecord object and the details
     * forwarded to the Eros logger supplied at construction, only if
     * <code>logRecord.getLevel() > LogLevel.INFO</code>.
     * <p>
     * The log record is then forwarded to the console output and file output
     * if the log record level is greater than their respective levels.
     * @param logRecord The LogRecord that should be forwarded to Eros.
     */
    public void publish(LogRecord logRecord) {
        long start = System.currentTimeMillis();
        if( logRecord.getLevel().intValue() >= publishLevel.intValue() ) {
            try {
                LogLevel logLevel = this.getErosLogLevel(logRecord.getLevel());
                String message = logRecord.getMessage();
                Throwable thrown = logRecord.getThrown();
                Object[] args = logRecord.getParameters();
                logger.log(message, logLevel, thrown, args);
            } catch (Exception exc) {
                this.getErrorManager().error("Error logging", exc,
                                        this.getErrorManager().WRITE_FAILURE);
            }
            if(debug)
                System.out.println("EROS - Eros Handler took - " + (System.currentTimeMillis()-start) + "ms");
        }

        start = System.currentTimeMillis();
        if( logRecord.getLevel().intValue() >= this.consoleLevel.intValue() ) {
            super.publish(logRecord);
        }
        if(debug)
            System.out.println("EROS - Console Handler took - " + (System.currentTimeMillis()-start) + "ms");

        if( fileOutput == null ) {
            try {
                this.createFileHandler(outputFile);
            } catch (Exception exc) {
                this.getErrorManager().error("Unable to open file output- " +
                                                outputFile, exc, 0);
            }
        }

        if( logRecord.getLevel().intValue() >= fileLevel.intValue() ) {
            start = System.currentTimeMillis();
            fileOutput.publish(logRecord);
            if(debug)
                System.out.println("EROS - File Handler took - " + (System.currentTimeMillis()-start) + "ms");
        }

        flush();
    }

    public void flush() {
        super.flush();
        fileOutput.flush();
    }

    public void close() {
        flush();
        fileOutput.close();
    }

    /**
     * Converts the LogRecord level into the appropriate Eros level.
     * @param level The LogRecord level to convert.
     * @return The corresponding Eros level.
     */
    public static LogLevel getErosLogLevel(Level level) {
        if(level != null) {
            if( level.equals(Level.SEVERE) ) {
                return LogLevel.ERROR;
            } else if( level.equals(Level.WARNING) ) {
                return LogLevel.WARN;
            } else if( level.equals(Level.INFO) ||
                       level.equals(Level.CONFIG) ) {
                return LogLevel.INFO;
            } else if( level.equals(Level.FINE) ||
                       level.equals(Level.FINER) ||
                       level.equals(Level.FINEST) ) {
                return LogLevel.DEBUG;
            }
        }
        return null;
    }

    /**
     * Unit Testing method.
     */
    public static void main(String[] args) {
        java.util.logging.Logger logger =null;
        try {
            System.setProperty("org.jini.projects.eros.logdir", "d:\\test");
            System.setProperty("org.jini.projects.eros.numlogs", "6");
            System.setProperty("org.jini.projects.eros.logsize", "1");

            ErosHandler eh = new ErosHandlerImpl(null, "AppName");
            logger = java.util.logging.Logger.getLogger("MyLogger");
            logger.setUseParentHandlers(false);
            //eh.setOutputDir(new File("d:\\logs"));
            eh.setConsoleLevel(Level.SEVERE);
            eh.setFileLevel(Level.FINE);
            logger.addHandler((Handler)eh);

            logger.log(java.util.logging.Level.WARNING, "My Message String");
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            new String().charAt(10);
        } catch (Exception exc) {
            logger.log(java.util.logging.Level.SEVERE, "Error", exc);
        }

        int count = 0;
        while(count <= 100) {
            logger.log(Level.WARNING, "message");
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
