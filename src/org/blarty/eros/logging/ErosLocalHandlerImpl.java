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
 * eros.jini.org : org.blarty.eros.logging
 * 
 * 
 * ErosLocalHandlerImpl.java
 * Created on 10-Jun-2004
 * 
 * ErosLocalHandlerImpl
 *
 */
package org.blarty.eros.logging;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import org.blarty.eros.ErosServiceImpl;
import org.blarty.eros.InstanceDetail;
import org.blarty.eros.InstanceDetailImpl;
import org.blarty.eros.LogLevel;
import org.blarty.eros.LoggingDetail;

/**
 * @author calum
 */
public class ErosLocalHandlerImpl
                extends
                StreamHandler {

        /**
         * The default size of the log files generated - 100KB.
         */
        public static final int DEFAULT_LOG_SIZE = 100;

        /**
         * The default number of generations of log files kept - 3.
         */
        public static final int DEFAULT_NUM_LOGS = 3;

        private static final Level DEFAULT_LEVEL = Level.INFO;

        private boolean debug = Boolean.getBoolean("org.blarty.eros.debug");

        private ErosServiceImpl logger = null;

        private String applicationName = null;

        private OutputFormatter formatter = new OutputFormatter();

        private FileHandler fileOutput = null;

        private Level consoleLevel = Level.INFO;

        private Level fileLevel = Level.INFO;

        private String outputFile = "";

        private String[] groups;

        private long instanceID = 0;

        /**
         * Creates the Handler with the Eros logger to use.
         * 
         * @param logger
         *                The Eros logger object which all logs will be
         *                forwarded to.
         * @param appName
         *                The name of the application that is requesting this
         *                logger.
         * @throws Exception
         *                 Any problems initialising the logger.
         */
        public ErosLocalHandlerImpl(ErosServiceImpl logger, String[] groups) throws Exception {
                super();
                this.logger = logger;
                InstanceDetail instanceDetail = new InstanceDetailImpl();
                instanceDetail.setApplicationName("Eros");
                String groupsString = null;
                StringBuffer sb = new StringBuffer();
                if (groups != null) {
                        for (int i = 0; i < groups.length; i++) {
                                sb.append(groups[i].toString() );
                                if(i!=groups.length-1){
                                        sb.append(", ");
                                }
                        }
                        groupsString = sb.toString();
                }
                instanceDetail.setErosGroups(groupsString);

                instanceID = logger.createInstanceRecord(instanceDetail);
                this.applicationName = "Eros";
                this.groups = groups;
                setFormatter(formatter);
                setOutputStream(System.err);

                String logDir = System.getProperty("org.blarty.eros.logdir");
                if (logDir != null) {
                        setOutputDir(new File(logDir));
                } else {
                        outputFile = applicationName + "%g.log";
                }

                setLevel(DEFAULT_LEVEL);
        }

        // Inherit comments
        public void setOutputDir(File loggingDir) throws IOException {
                if (loggingDir.isDirectory()) {
                        outputFile = loggingDir.getAbsolutePath() + File.separator + applicationName + "%g.log";
     
                        createFileHandler(outputFile);
                } else {
                        throw new IOException("Unable to write log file to " + "directory specified - " + loggingDir.getAbsolutePath());
                }
        }

        /**
         * Creates the file handler that will be used.
         * 
         * @param outFile
         *                The path for the file.
         * @throws IOException
         *                 Problems creating the file.
         */
        private void createFileHandler(String outFile) throws IOException {
                Integer logSize = Integer.getInteger("org.blarty.eros.logsize");
                if (logSize == null) {
                        logSize = new Integer(DEFAULT_LOG_SIZE);
                }

                Integer logs = Integer.getInteger("org.blarty.eros.numlogs");
                if (logs == null) {
                        logs = new Integer(DEFAULT_NUM_LOGS);
                }

                fileOutput = new FileHandler(outFile, logSize.intValue() * 1000, logs.intValue(), false);
                fileOutput.setFormatter(formatter);
                fileOutput.setLevel(fileLevel);
        }

        // Inherit comments
        public Level getLevel() {
                return Level.ALL;
        }

        // Inherit comments
        public void setLevel(Level level) {
                if (debug)
                        System.out.println("EROS Internal Logging - Setting level to " + level.getName());
                setConsoleLevel(level);
                setFileLevel(level);
        }

        // Inherit comments
        public void setConsoleLevel(Level level) {
                if (debug)
                        System.out.println("EROS - Setting Console level to " + level.getName());
                this.consoleLevel = level;
        }

        // Inherit comments
        public void setFileLevel(Level level) {
                fileLevel = level;
                if (fileOutput != null) {
                        if (debug)
                                System.out.println("EROS - Setting File level to " + level.getName());
                        fileOutput.setLevel(level);
                }
        }

        /**
         * Determines if the Handler will publish the LogRecord. This always
         * returns true as this handler may have different levels for its output
         * destinations.
         * 
         * @param logRec
         *                The LogRecord which should be published.
         * @return Always returns true.
         */
        public boolean isLoggable(LogRecord logRec) {
                return true;
        }

        /**
         * Publishes the record supplied. The data is extracted from the
         * LogRecord object and the details forwarded to the Eros logger
         * supplied at construction, only if
         * <code>logRecord.getLevel() > LogLevel.INFO</code>.
         * <p>
         * The log record is then forwarded to the console output and file
         * output if the log record level is greater than their respective
         * levels.
         * 
         * @param logRecord
         *                The LogRecord that should be forwarded to Eros.
         */
        public void publish(LogRecord logRecord) {
                long start = System.currentTimeMillis();
                if (logRecord.getLevel().intValue() > Level.INFO.intValue()) {
                        try {
                                LogLevel logLevel = this.getErosLogLevel(logRecord.getLevel());
                                String message = logRecord.getMessage();
                                Throwable thrown = logRecord.getThrown();
                                Object[] args = logRecord.getParameters();
                                doLog(message, logLevel, thrown, args, 0);
                        } catch (Exception exc) {
                                this.getErrorManager().error("Error logging", exc, this.getErrorManager().WRITE_FAILURE);
                        }
                        if (debug)
                                System.out.println("EROS - Eros Handler took - " + (System.currentTimeMillis() - start) + "ms");
                }

                start = System.currentTimeMillis();
                if (logRecord.getLevel().intValue() >= this.consoleLevel.intValue()) {
                        super.publish(logRecord);
                }
                if (debug)
                        System.out.println("EROS - Console Handler took - " + (System.currentTimeMillis() - start) + "ms");

                if (fileOutput == null) {
                        try {
                                this.createFileHandler(outputFile);
                        } catch (Exception exc) {
                                this.getErrorManager().error("Unable to open file output- " + outputFile, exc, 0);
                        }
                }

                if (logRecord.getLevel().intValue() >= fileLevel.intValue()) {
                        start = System.currentTimeMillis();
                        fileOutput.publish(logRecord);
                        if (debug)
                                System.out.println("EROS - File Handler took - " + (System.currentTimeMillis() - start) + "ms");
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
         * 
         * @param level
         *                The LogRecord level to convert.
         * @return The corresponding Eros level.
         */
        public static LogLevel getErosLogLevel(Level level) {
                if (level != null) {
                        if (level.equals(Level.SEVERE)) {
                                return LogLevel.ERROR;
                        } else if (level.equals(Level.WARNING)) {
                                return LogLevel.WARN;
                        } else if (level.equals(Level.INFO) || level.equals(Level.CONFIG)) {
                                return LogLevel.INFO;
                        } else if (level.equals(Level.FINE) || level.equals(Level.FINER) || level.equals(Level.FINEST)) {
                                return LogLevel.DEBUG;
                        }
                }
                return null;
        }

        /**
         * Unit Testing method.
         */
        public static void main(String[] args) {
                java.util.logging.Logger logger = null;
                try {
                        System.setProperty("org.blarty.eros.logdir", "d:\\test");
                        System.setProperty("org.blarty.eros.numlogs", "6");
                        System.setProperty("org.blarty.eros.logsize", "1");

                        ErosHandler eh = new ErosHandlerImpl(null, "AppName");
                        logger = java.util.logging.Logger.getLogger("MyLogger");
                        logger.setUseParentHandlers(false);
                        // eh.setOutputDir(new File("d:\\logs"));
                        eh.setConsoleLevel(Level.SEVERE);
                        eh.setFileLevel(Level.FINE);
                        logger.addHandler((Handler) eh);

                        logger.log(java.util.logging.Level.WARNING, "My Message String");
                } catch (Exception e) {
                        e.printStackTrace();
                }

                try {
                        new String().charAt(10);
                } catch (Exception exc) {
                        logger.log(java.util.logging.Level.SEVERE, "Error", exc);
                }

                int count = 0;
                while (count <= 100) {
                        logger.log(Level.WARNING, "message");
                        try {
                                Thread.sleep(500);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }

        private void doLog(String message, LogLevel level, Throwable exception, Object[] args, int code) {

                LoggingDetail logDetail = new LoggingDetail();

                logDetail.setDate(new java.util.Date());

                // Populate the logging object with the user supplied
                // information

                message = message.replaceAll("<", "&lt;");
                message = message.replaceAll(">", "&gt;");
                logDetail.setMessage(message);

                logDetail.setLevel(level);

                // If no exception has been supplied generate a stack trace to
                // supply the class, method and line number of the log.

                if (exception == null) {
                        exception = buildRuntimeInformationException();
                        logDetail.setExceptionName("Runtime Info");
                } else {
                        logDetail.setExceptionName(exception.getClass().getName());
                }
                String stackTrace = "";
                try {
                        java.io.StringWriter sw = new java.io.StringWriter();
                        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                        exception.printStackTrace(pw);
                        stackTrace = sw.toString();
                        // Not sure if we need to close these?
                        sw.close();
                        pw.close();

                } catch (Exception exc) {
                        if (debug)
                                System.out.println("EROS - Unable to get stack trace.");
                        stackTrace = "Unable to retrieve StackTrace";
                }
                stackTrace = stackTrace.replaceAll("<", "&lt;");
                stackTrace = stackTrace.replaceAll(">", "&gt;");

                logDetail.setStackTrace(stackTrace);

                logDetail.setArguments(args);

                logDetail.setCode(new Integer(code));

                // Populate any other fields

                StackTraceElement[] elements = exception.getStackTrace();
                StackTraceElement actualThrowingElement = elements[0];

                // If we have a RuntimeInfo exception, we have to walk down the
                // stack trace until we find the required stack element to get
                // the class and method information from
                actualThrowingElement = elements[0];
                if (elements[0].getClassName().equals("org.blarty.eros.ErosSmartLogger") && elements[0].getMethodName().equals("buildRuntimeInformationException")) {
                        for (int i = 1; i < elements.length; i++) {
                                if (elements[i].getClassName().equals("org.blarty.eros.ErosSmartLogger") || elements[i].getClassName().equals("org.blarty.eros.logging.ErosHandlerImpl") || elements[i].getClassName().equals("java.util.logging.Logger")) {
                                        // Skip over
                                } else {
                                        actualThrowingElement = elements[i];
                                        break;
                                }
                        }
                }
             
                logDetail.setClassName(actualThrowingElement.getClassName());
                
                logDetail.setMethodName(actualThrowingElement.getMethodName());

                logDetail.setInstanceIdentifier(this.instanceID);

                try {
                        logger.log(logDetail);
                } catch (RemoteException e) {
                        // TODO Handle RemoteException
                        e.printStackTrace();
                }
        }

        private Throwable buildRuntimeInformationException() {
                Throwable exception;
                exception = new org.blarty.eros.exception.RuntimeInfo();
                exception.fillInStackTrace();

                return exception;
        }
}
