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
package org.jini.projects.eros;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Level;

import net.jini.core.lookup.ServiceID;
import org.jini.projects.eros.logging.ErosLoggerImpl;

/**
 * Smart proxy for use by clients to log errors with a central server. This is
 * the logger implementation that is returned from the service when a request
 * for a logger is made. This class handles all the client side implementation
 * for ensuring all logs are eventually sent to a server.
 */
public class ErosSmartLogger implements Serializable,ErosLogger {

        static final long serialVersionUID = 1191867620218659520L;

        // Singletom static instance variable
        private static ErosSmartLogger logger = null;

        private ErosLogServer logServer = null;

        private String[] groups = null;

        private ErosServiceMonitor monitor = null;

        // private ErosServiceMonitorIntf exportedMonitor = null;
        private String appName = null;

        private boolean debug = true;

        private transient OutputStreamWriter outWriter = null;

        private LogLevel loggingLevel = LogLevel.INFO;

        private ServiceID origServID = null;

        private Level publishLevel = Level.WARNING;

        private long instanceID;

        private boolean instanceBound = false;

        /**
         * Required for use as a standalone utility class.
         */
        private ErosSmartLogger(String[] groups) throws Exception {
                this.groups = groups;
        }

        /**
         * Used when instantiated by the Eros service.
         * 
         * @param logger
         *                The server that should be used initially to log with
         * @param servID
         *                The ServiceID for the server supplied, is required for
         *                monitoring joining/leaving services.
         * @param grps
         *                The Jini groups that should be used for monitoring
         *                Eros servers
         * @throws Exception
         *                 Any problems initialising the object.
         */
        protected ErosSmartLogger(ErosLogServer logger, ServiceID servID, String[] grps) throws Exception {
                this.logServer = logger;
                this.origServID = servID;
                this.groups = grps;
        }

        /**
         * Singleton creation method. Provides a instance of an ErosLogger, to a
         * client and ensures only one instance of this object in the JVM.
         * 
         * @param groups
         *                The Jini groups that the logger should search for Eros
         *                servers within.
         * @throws Exception
         *                 Any problems creating the ErosLogger.
         */
        public static synchronized ErosLogger getLogger(String[] groups) throws Exception {
                if (logger == null) {
                        logger = new ErosSmartLogger(groups);
                        logger.initialise(null);
                }
                return logger;
        }

        public void setPublishingLevel(Level publishLevel) {
                this.publishLevel = publishLevel;

        }

        /*
         * Sets up the logger. This should be called by all clients before they
         * attempt to perform any logging. @param name The name of the
         * application that will be using this logger. This name could be used
         * for categorisation at the server. <p><i> Required to ensure the
         * <code>ErosServiceMonitor</code> is instantiated in the client JVM,
         * and not in the server JVM and then serialized.</i></p>
         */
        // Inherit Javadoc comments
        public void initialise(String name) throws RemoteException {
                this.appName = name;
                System.out.println("Initialised application name to: " + this.appName);
                debug = Boolean.getBoolean("org.jini.projects.eros.debug");

                if (debug)
                        System.out.println("EROS - SmartLogger using groups - " + ErosServiceMonitor.getGroups(this.groups));

                try {
                        outWriter = new OutputStreamWriter(System.out);
                } catch (Exception exc) {
                        // Ignore
                        if (debug)
                                exc.printStackTrace();
                }

                // Create the eros service monitor
                ErosServiceMonitor theBackend;
                if (this.logServer != null) {
                        theBackend = new ErosServiceMonitor(logServer, origServID, this.groups);
                } else {
                        theBackend = new ErosServiceMonitor(this.groups);
                }
                monitor = theBackend;

                // Create the local file processing thread
                ErosFileProcessor.startFileProcessor(theBackend);
                if (!instanceBound) {
                        bindInstanceInfo();
                }
                // Create a default logger
                try {
                        if (isLoggingVersion()) {
                                java.util.logging.LogManager manager = java.util.logging.LogManager.getLogManager();
                                if (manager.getLogger(ErosLogger.EROS_LOGGER_NAME) == null) {
                                        java.util.logging.Logger erosLogger = ErosLoggerImpl.getLogger(this, appName);
                                        manager.addLogger(erosLogger);
                                        if (debug)
                                                System.out.println("EROS - Added default logger to manager");
                                }
                        }
                } catch (Exception e) {
                        throw new RemoteException("Unable to create default ErosLogger", e);
                }

                try {
                        // Allows time for discovery before any logging occurs
                        Thread.sleep(500);
                } catch (Exception exc) {
                }
        }

        private void bindInstanceInfo() {
                InstanceDetail instDetail = buildInstanceInfo();
                if (instDetail != null)
                        instanceID = monitor.logInstanceInfo(buildInstanceInfo());
                if (instanceID == -1)
                        instanceBound = false;
                else
                        instanceBound = true;
        }

        private InstanceDetail buildInstanceInfo() {
                // TODO Auto-generated method stub
                try {
                        InstanceDetail detail = new InstanceDetailImpl();
                        detail.setApplicationName(this.appName);
                        String groupsString = null;
                        StringBuffer sb = new StringBuffer();
                        if (groups != null) {
                                for (int i = 0; i < groups.length; i++) {
                                        sb.append(groups[i].toString());
                                        if (i != groups.length - 1) {
                                                sb.append(", ");
                                        }
                                }
                                groupsString = sb.toString();
                        }
                        detail.setErosGroups(groupsString);

                        return detail;
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                return null;
        }

        // Inherit comments
        public void terminate() {
                if (monitor != null)
                        monitor.terminate();
        }

        // Inherit javadocs from ErosLogger interface
        public Object getLoggingHandler() throws Exception {
                if (isLoggingVersion()) {
                        if (debug)
                                System.out.println("EROS - Returning new handler");
                        return new org.jini.projects.eros.logging.ErosHandlerImpl(this, this.appName, this.publishLevel);
                } else {
                        if (debug)
                                System.out.println("EROS - No handler created, wrong java version");
                        return null;
                }
        }

        private boolean isLoggingVersion() {
                double vrs = 0;
                double loggingVrs = 1.4;
                try {
                        String vers = System.getProperty("java.version");
                        if (debug)
                                System.out.println("EROS - Java Version found = " + vers);
                        vers = vers.substring(0, vers.indexOf(".") + 2);
                        vrs = Double.parseDouble(vers);
                        if (debug)
                                System.out.println("EROS - Java version numeric = " + vrs);
                } catch (Exception exc) {
                        exc.printStackTrace();
                }
                return (vrs >= loggingVrs);
        }

        /*
         * Inherit all log method javadocs from ErosLogger interface
         */
        public void logWarning(String message) {
                log(message, LogLevel.WARN);
        }

        public void log(String message, LogLevel level) {
                doLog(message, level, null, null, 0);
        }

        public void log(String message, LogLevel level, Throwable exception) {
                doLog(message, level, exception, null, 0);
        }

        public void log(String message, LogLevel level, Throwable exception, Object[] args) {
                doLog(message, level, exception, args, 0);
        }

        public void log(String message, LogLevel level, Throwable exception, Object[] args, int code) {
                doLog(message, level, exception, args, code);
        }

        /**
         * The internal method that is used to perform the log.
         */
        private void doLog(String message, LogLevel level, Throwable exception, Object[] args, int code) {
                /**
                 * When created by the service, no monitor is instantiated as
                 * this must be done in the client JVM when the client calls
                 * initialise(). Therefore this instantiation takes place on the
                 * first call to log. Provided as a backup if initialise() is
                 * not called first by the client. Placed first to allow time
                 * for discovery before logging.
                 */
                long start = System.currentTimeMillis();

                try {
                        if (monitor == null) {
                                initialise(this.appName);
                        }
                } catch (Exception exc) {
                        // Can't create monitor, exception will be caught when
                        // trying to
                        // retrieve the current logger.
                }

                long detailStart = System.currentTimeMillis();

                // First populate the logging detail object.
                LogDetail logDetail = null;
                try {
                        // Create the logging detail object with the information
                        // supplied
                        logDetail = new LoggingDetail();
                        // System.out.println("PreDetails: " + logDetail);
                        logDetail.setDate(new java.util.Date());

                        // Populate the logging object with the user supplied
                        // information

                        message = message.replaceAll("<", "&lt;");
                        message = message.replaceAll(">", "&gt;");
                        logDetail.setMessage(message);

                        logDetail.setLevel(level);

                        // If no exception has been supplied generate a stack
                        // trace to
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
                        // System.out.println("Setting arguments to " + args);
                        logDetail.setArguments(args);
                        // System.out.println("Setting code to " + code);
                        logDetail.setCode(new Integer(code));

                        StackTraceElement[] elements = exception.getStackTrace();
                        StackTraceElement actualThrowingElement = elements[0];

                        // If we have a RuntimeInfo exception, we have to walk
                        // down the stack trace until we find the required stack
                        // element to get the class and method information from
                        if (elements[0].getClassName().equals("org.jini.projects.eros.ErosSmartLogger") && elements[0].getMethodName().equals("buildRuntimeInformationException")) {
                                for (int i = 1; i < elements.length; i++) {
                                        if (elements[i].getClassName().equals("org.jini.projects.eros.ErosSmartLogger") || elements[i].getClassName().equals("org.jini.projects.eros.logging.ErosHandlerImpl") || elements[i].getClassName().equals("java.util.logging.Logger")) {
                                                // Skip over
                                        } else {
                                                actualThrowingElement = elements[i];
                                                break;
                                        }
                                }
                        }

                        logDetail.setClassName(actualThrowingElement.getClassName());
                        logDetail.setMethodName(actualThrowingElement.getMethodName());
                        // Populate any other fields
                        // System.out.println("Setting groups to " + groups);
                        logDetail.setInstanceIdentifier(this.instanceID);

                } catch (Exception exc) {
                        System.out.println("Exception detail: " + logDetail);
                        System.out.println(exc.getMessage());
                        exc.printStackTrace();
                        if (debug) {
                                System.out.println("EROS - Error populating logging detail");
                                exc.printStackTrace();
                        }
                }

                if (debug) {
                        System.out.println("EROS - Detail time taken = " + (System.currentTimeMillis() - detailStart));
                }

                long fileStart = System.currentTimeMillis();
                /**
                 * Persist the logging detail to disk
                 */
                File outFile = null;
                if (logDetail != null) {
                        outFile = ErosFileProcessor.persistDetails(logDetail);
                } else {
                        if (debug)
                                System.out.println("EROS - No logging detail to persist or log.");
                        // Ignore this error, as we still want to try ang log,
                        // if logging
                        // fails then we should deal with this error.
                }

                if (debug) {
                        System.out.println("EROS - FileSave time taken = " + (System.currentTimeMillis() - fileStart));
                }

                long logStart = System.currentTimeMillis();
                if (!instanceBound)
                        bindInstanceInfo();
                if (instanceBound) {
                        if (monitor != null && monitor.log(logDetail)) {
                                if (debug) {
                                        System.out.println("EROS - Logged time taken = " + (System.currentTimeMillis() - logStart));
                                }
                                ErosFileProcessor.deleteFile(outFile);
                        } else {
                                if (outFile != null) {
                                        ErosFileProcessor.createErrorFile(outFile);
                                } else {
                                        if (debug)
                                                System.out.println("EROS - !!!!!!!!!!!!!!!!!!!!! " + "No details persisted, log lost!!!!!!!!!!!!!!!!!!!!");
                                }
                        }
                } else {
                        if (outFile != null) {
                                ErosFileProcessor.createErrorFile(outFile);
                        } else {
                                if (debug)
                                        System.out.println("EROS - !!!!!!!!!!!!!!!!!!!!! " + "No details persisted, log lost!!!!!!!!!!!!!!!!!!!!");
                        }
                }
                // Last but not least log to the standard out, and file if
                // required
                // writeLog(logDetail);

                // System.gc();

                if (debug) {
                        System.out.println("EROS - Overall time taken = " + (System.currentTimeMillis() - start));
                }
        }

        private Throwable buildRuntimeInformationException() {
                Throwable exception;
                exception = new org.jini.projects.eros.exception.RuntimeInfo();
                exception.fillInStackTrace();

                return exception;
        }

        /**
         * // * Method that writes the logging detail to an output stream // *
         * 
         * @deprecated This functionality can now be obtained by using the // *
         *             java.util.logging packages. //
         */
        // private void writeLog(LogDetail logDetail) {
        // try {
        // StringBuffer sb = new StringBuffer();
        // if( outWriter != null && logDetail != null ) {
        //                
        // sb.append(new java.text.SimpleDateFormat(
        // "[dd/MM/yyyy HH:mm:ss]").format(logDetail.getDate()) + " ");
        // sb.append("[" + logDetail.getCode() + "] ");
        // sb.append("[" + logDetail.getApplicationName() + ":");
        // String className = logDetail.getClass().getName();
        // sb.append(className.substring(className.lastIndexOf('.',className.length())+1)
        // + "] ");
        // sb.append(logDetail.getMessage());
        // //sb.append("\n");
        // this.outWriter.write(sb.toString());
        // System.out.println(sb.toString());
        // }
        // } catch (Exception exc) {
        // if(debug) exc.printStackTrace();
        // //Any exceptions should be caught and dealt with gracefully.
        // }
        // }
        /**
         * Replicate the actions of a client using this class.
         */
        public static void main(String[] args) {
                try {
                        ErosLogger esl = ErosSmartLogger.getLogger(new String[] { "chris" });
                        esl.initialise("MyClientApp");
                        synchronized (esl) {
                                esl.wait(10000);
                        }
                        esl.getLoggingHandler();
                        esl.log("My message", LogLevel.ERROR);

                } catch (Exception exc) {
                        exc.printStackTrace();
                }
                System.exit(0);
        }

}
