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
 * Title: Eros Logging Service
 * <p>
 * Description: Project to provide distributed system logging.
 * <p>
 * Copyright: Copyright (c) C. Lunn
 * <p>
 * Company: Countrywide Assured
 * <p>
 * 
 * @author C. Lunn
 * @version 1.0
 */

package org.jini.projects.eros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.activation.ActivationID;
import java.rmi.server.RemoteObject;
import java.security.PrivilegedExceptionAction;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lookup.entry.Location;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.attribute.UIFactoryTypes;
import net.jini.lookup.ui.factory.JComponentFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.projects.eros.admin.ErosAdmin;
import org.jini.projects.eros.admin.ErosAdminImpl;
import org.jini.projects.eros.constrainable.ErosAdminProxy;
import org.jini.projects.eros.constrainable.ErosProxy;
import org.jini.projects.eros.exception.StoreUnavailableException;
import org.jini.projects.eros.logging.ErosLocalHandlerImpl;
// import org.jini.projects.eros.server.CentralStore;
import org.jini.projects.eros.server.ErosBackendStore;
import org.jini.projects.eros.ui.ErosMainUI;
import org.jini.projects.eros.ui.ErosMainUIFactory;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;



import com.sun.jini.reliableLog.LogHandler;
import com.sun.jini.reliableLog.ReliableLog;

// Eros service imports
// ServiceUI imports
/**
 * Main service class for the Eros Service. Provides the lookup and join
 * functions for the service.
 */
public class ErosServiceImpl implements ErosInterface,Remote {
        /**
         * The name that is used for this service.
         */
        public final static String SERVICENAME = "ErosService";

        /**
         * The temporary store directory for logs that cannot be loaded to the
         * database.
         */
        public final static String STORE_DIR = System.getProperty("user.dir") + File.separator + "store";

        /**
         * The threshold for the number of updates that forces a complete
         * snapshot of the persisted data for the service through reliable log
         */
        private final static long UPDATE_THRESHOLD = 25;

        /*
         * Jini 2 modifications
         * 
         * 
         */
        private static ErosServiceImpl service;

        protected Configuration config = null;

        protected ErosInterface serverProxy;

        protected ErosInterface smartProxy;

        private static ErosServiceImpl.ShutdownThread erosShutDown = null;

        private ActivationID activationID = null;

        // Groups need to be initialised to stop joining of 'ALL.GROUPS'
        private String[] groups = new String[] {};

        private Entry[] attribs = null;

        private LookupDiscoveryManager ldm = null;

        private JoinManager jm = null;

        private ServiceID servID = null;

        private Logger logger = null;

        private ErosSmartLogger smartEros = null;

        private RemoteObject meExported = null;

        private transient ErosAdmin erosAdmin = null;

        private transient ErosAdmin exportedAdmin = null;

        private ReliableLog log = null;

        private Vector workQue = new Vector();

        private Vector errorQue = new Vector();

        private ErosBackendStore store = null;

        private Map instanceMap = new HashMap();

        // private ErosBackendStore helpdeskStore = null;
        private long logCount = 0;

        private ErosServiceStats stats = null;

        private Boolean processing = new Boolean(true);

        private long workerProcessedCount = 0;

        private Exporter exp;

        private JoinManager manager;

        private LookupDiscoveryManager discoveryManager;

        protected ErosServiceImpl(String[] configOptions) throws Exception {
                config = ConfigurationProvider.getInstance(configOptions);
                service = this;
                this.groups = (String[]) config.getEntry("org.jini.projects.eros", "groups", String[].class, new String[] { "public" });

                printGroups();
                init();
                printGroups();

        }

        private void printGroups() {

                /*
                 * System.out.println("Groups:"); for(int i=0;i<groups.length;i++)
                 * System.out.println("\t"+groups[i]);
                 */

        }

        /**
         * Non-activation constructor.
         * 
         * @param groups
         *                The Jini groups which the service should join.
         * @param logDir
         *                The directory where all logs should be persisted
         * @throws RemoteException
         *                 An exceptions
         */
        public ErosServiceImpl(String[] groups, String logDir) throws RemoteException {
                // meExported = UnicastRemoteObject.exportObject(this);
                this.groups = groups;
                ErosAdmin admin = (ErosAdmin) getAdmin();
                admin.setLookupGroups(groups);

        }

        protected void init() throws Exception {
                LoginContext loginContext = (LoginContext) config.getEntry("org.jini.projects.eros", "loginContext", LoginContext.class, null);
                if (loginContext == null) {
                        initAsSubject();
                } else {
                        loginContext.login();
                        Subject.doAsPrivileged(loginContext.getSubject(), new PrivilegedExceptionAction() {
                                public Object run() throws Exception {
                                        initAsSubject();
                                        return null;
                                }
                        }, null);
                }
        }

        protected void initAsSubject() throws Exception {
                try {
                        printGroups();

                        setUpLogs("data");

                        printGroups();
                        logger.info("Eros initialisation started........");
                        /**
                         * This has been removed as the amount of memory used by
                         * this output cannot be controlled
                         * 
                         * //Set up the internal logger for passing logging
                         * //information to the serviceui. PatternLayout layout =
                         * new PatternLayout("[%-5p] (%d{dd.MM.yy HH:mm:ss})
                         * %c{1} %x - %m\n"); internalLog = new
                         * StringWriter(MAX_UI_LOG_SIZE); WriterAppender writer =
                         * new WriterAppender(layout, internalLog);
                         * logger.addAppender(writer);
                         */
                        // End of Log4J
                        examineQueues();
                        setupAttributes();
                        if (exp == null)
                                exp = getExporter();
                        serverProxy = (ErosInterface) exp.export(this);
                        smartProxy = ErosProxy.create(serverProxy, UuidFactory.generate());

                        discoveryManager = (LookupDiscoveryManager) config.getEntry("org.jini.projects.eros", "discoveryManager", LookupDiscoveryManager.class);

                        manager = new JoinManager(smartProxy, this.attribs, getServiceID(), discoveryManager, null, config);
                        erosAdmin = new ErosAdminImpl(null, this, manager, discoveryManager);
                        logger.fine("Registering shutdown hook");
                        erosShutDown = new ShutdownThread();
                        Runtime.getRuntime().addShutdownHook(erosShutDown);

                        logger.info("Completed lookup & join.");

                } catch (Exception e) {
                        e.printStackTrace();
                        System.exit(1);
                }
        }

        protected Exporter getExporter() throws ConfigurationException, RemoteException {
                return (Exporter) config.getEntry("org.jini.projects.eros", "exporter", Exporter.class, new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory()));
        }

        protected ServiceID getServiceID() {
                return createServiceID();
        }

        protected static ServiceID createServiceID() {
                Uuid uuid = UuidFactory.generate();

                return new ServiceID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        }

        private void setupAttributes() {

                if (this.attribs == null || this.attribs.length == 0) {
                        attribs = new Entry[4];
                        attribs[0] = new Name(SERVICENAME);
                        attribs[1] = new Location(" ", " ", "");
                        attribs[1] = new ServiceInfo(ErosServiceImpl.SERVICENAME, "Jini.org", " ", "1.0", "Community", "");
                        attribs[2] = new ErosServiceType();
                        try {
                                // MainUI
                                UIDescriptor uiDescMain = new UIDescriptor(ErosMainUI.ROLE, ErosMainUIFactory.TOOLKIT, null, new MarshalledObject(new ErosMainUIFactory()));
                                uiDescMain.attributes = new java.util.HashSet();
                                uiDescMain.attributes.add(new UIFactoryTypes(java.util.Collections.singleton(JComponentFactory.TYPE_NAME)));
                                attribs[3] = uiDescMain;
                        } catch (Exception exc) {
                                logger.log(Level.WARNING, "Unable to create ServiceUI", exc);
                        }

                }

                StringBuffer sb = new StringBuffer();
                if (groups != null) {
                        logger.fine("Groups size = " + groups.length);
                        for (int j = 0; j < groups.length; j++) {
                                sb.append(groups[j]);
                                sb.append(",");
                        }
                        logger.fine("Server using groups = " + sb.toString());
                } else {
                        logger.fine("No groups supplied at start-up!!");
                }
        }

        private void examineQueues() {
                if (this.workQue != null)
                        logger.info("Number of items recovered in work queue = " + this.workQue.size());
                if (this.errorQue != null)
                        logger.info("Number of items recovered in error queue = " + this.errorQue.size());
                if (System.getSecurityManager() == null)
                        System.setSecurityManager(new RMISecurityManager());
                // helpdeskStore = new HelpdeskStore();
                File store = new File(STORE_DIR);
                logger.info("Using store directory - " + store.getAbsolutePath());
                store.mkdirs();
                if (stats == null) {
                        stats = new ErosServiceStats();
                        stats.setCentralStoreInfo(store.toString());
                }
                // Start the worker threads
                WorkQueThread workThread = new WorkQueThread();
                workThread.start();
                ErrorQueWorker errorThread = new ErrorQueWorker();
                errorThread.start();
        }

        private void setUpLogs(String logDir) throws IOException, Exception {
                // Recover any state using reliable log.
                log = new ReliableLog(logDir, new ErosLogHandler());
                log.recover();
                log.snapshot();
                String storeClass = (String) config.getEntry("org.jini.projects.eros", "storeClass", String.class);
                try {
                        ErosBackendStore tStore = (ErosBackendStore) Class.forName(storeClass).newInstance();
                        store = tStore;
                } catch (InstantiationException e) {
                        // TODO Handle InstantiationException
                        e.printStackTrace();
                } catch (IllegalAccessException e) {
                        // TODO Handle IllegalAccessException
                        e.printStackTrace();
                } catch (ClassNotFoundException e) {
                        // TODO Handle ClassNotFoundException
                        e.printStackTrace();
                }
                if (store == null) {
                        System.out.println("FATAL ERROR: Store could not be initialised");
                        System.exit(0);
                }
                if (stats == null) {
                        stats = new ErosServiceStats();
                        stats.setCentralStoreInfo(store.toString());
                }

                printGroups();
                // Setup the root logger and handlers
                Logger rootLogger = Logger.getLogger("org.jini.projects.eros");

                rootLogger.setUseParentHandlers(false);

                printGroups();
                HashMap parameters = new HashMap();
                parameters.put("region", this.groups);

                // store.initialise(rootLogger,parameters);

                Level logLevel = Level.INFO;
                if (Boolean.getBoolean("uk.co.cwa.debug")) {
                        logLevel = Level.FINE;
                }
                ErosLocalHandlerImpl localHandler = new ErosLocalHandlerImpl(this, this.groups);
                localHandler.setOutputDir(new File("logs"));
                localHandler.setLevel(logLevel);
                rootLogger.addHandler(localHandler);

                rootLogger.setLevel(logLevel);
                // fileHandler.setLevel(logLevel);
                // consoleHandler.setLevel(logLevel);
                logger = Logger.getLogger(this.getClass().getName());
                logger.setLevel(logLevel);
        }

        // Inherit javadocs from <code>ErosService</code> interface.
        public ErosLogger getLogger() throws RemoteException {
                try {

                        if (this.smartEros == null) {
                                try {
                                        smartEros = new ErosSmartLogger(this.smartProxy, this.servID, groups);
                                } catch (Exception exc) {
                                        System.out.println("Exception thrown!!! (instantiating logger)");
                                        logger.log(Level.WARNING, "Unable to instantiate Logger", exc);
                                        throw new RemoteException("Unable to supply Logger", exc);
                                }
                        }

                        stats.updateLoggersSupplied();
                        ErosStatsChangeRecord statsRecord = new ErosStatsChangeRecord();
                        statsRecord.stats = stats;
                        synchronized (log) {
                                try {
                                        log.update(statsRecord, true);
                                } catch (Exception exc) {
                                        System.out.println("Exception thrown (logging stats change)!!!");
                                        logger.log(Level.WARNING, "Error logging stats change.", exc);
                                }
                        }
                } catch (Exception ex) {
                        ex.printStackTrace();

                }
                return this.smartEros;
        }

        /**
         * Provides a synchronized update to the internal count of logs
         */
        private synchronized long getNextLogCount() {
                try {
                        logCount++;
                } catch (Exception exc) {
                        logCount = 0;
                }
                return logCount;
        }

        // Inherit javadocs from <code>ErosLogServer</code> interface
        public synchronized void log(LogDetail logDetail) throws RemoteException {
                long start = System.currentTimeMillis();
                long end = 0;
                if (processing.booleanValue()) {
                        long count = this.getNextLogCount();
                        try {
                                // logDetail.setGroups(this.groups);
                                this.workQue.add(logDetail);
                                ErosWorkItemAdded logRecord = new ErosWorkItemAdded();
                                logRecord.logDetail = logDetail;
                                long logSyncStart = System.currentTimeMillis();
                                synchronized (log) {
                                        logger.fine("Log waited for sync = " + (System.currentTimeMillis() - logSyncStart));
                                        log.update(logRecord, true);
                                }
                        } catch (Exception exc) {
                                logger.log(Level.WARNING, "Error occured with ReliableLog", exc);
                        }
                        end = System.currentTimeMillis();
                        logger.fine("Server log no. - " + count + ", level = " + logDetail.getLevel() + " time taken=" + (end - start) + "ms");
                } else {
                        throw new ServerException("Server shutting down, unable to log message.");
                }
        }

        public long createInstanceRecord(InstanceDetail instanceDetail) throws RemoteException {
                // TODO Auto-generated method stub
                try {
                        return store.createInstanceRecord(instanceDetail);
                } catch (Exception ex) {
                        throw new RemoteException(ex.getMessage());
                }

        }

        /**
         * Provides the administration object for the service.
         * 
         * @throws RemoteException
         *                 Any errors obtaining the administration object
         * @return The administration object for the service
         */
        public Object getAdmin() throws RemoteException {
                // URGENT : add admin changes
                // return null;
                try {
                        if (exportedAdmin == null) {

                                Exporter exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory());
                                Remote proxy = exporter.export(this.erosAdmin);
                                ErosAdmin Eadmin = ErosAdminProxy.create((ErosAdmin) proxy, UuidFactory.generate());
                                exportedAdmin = Eadmin;

                        }
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
                return exportedAdmin;
        }

        /**
         * Sets the Jini groups that this service should participate in.
         * 
         * @param groups
         *                The Jini groups
         */
        public void setGroups(String[] groups) {
                this.groups = groups;
                try {
                        ErosGroupsChangedRecord logRecord = new ErosGroupsChangedRecord();
                        logRecord.groups = this.groups;
                        synchronized (log) {
                                log.update(logRecord, true);
                        }
                } catch (Exception exc) {
                        exc.printStackTrace();
                }
        }

        /**
         * Sets the attributes for the service that will be published to any
         * available lookup services.
         * 
         * @param attribs
         *                The attributes relating to the service
         */
        public void setAttributes(Entry[] attribs) {
                this.attribs = attribs;
                try {
                        ErosAttribsChangedRecord logRecord = new ErosAttribsChangedRecord();
                        logRecord.attribs = ErosServiceImpl.marshalAttributes(this.attribs);
                        synchronized (log) {
                                log.update(logRecord, true);
                        }
                } catch (Exception exc) {
                        exc.printStackTrace();
                }
        }

        /**
         * Allows all the persistant data for the service to be removed.
         */
        public void removeLogs() {
                synchronized (log) {
                        log.deletePersistentStore();
                }
        }

        // Inherit comments from ErosServiceUI
        public ErosServiceStats getStats() throws RemoteException {
                /*
                 * //Only return the last 2000 bytes of the log as part of the
                 * stats //object. int start = 0; int end =
                 * internalLog.toString().length();
                 * if(internalLog.toString().length() > MAX_UI_LOG_SIZE) start =
                 * internalLog.toString().length() - MAX_UI_LOG_SIZE;
                 * stats.setInternalLogging(internalLog.toString().substring(start,end));
                 */
                stats.setInternalLogging("Feature Currently Unavailable");
                // Set the info message for the central store.
                stats.setCentralStoreInfo(store.toString());
                stats.setErrorQueSize(this.errorQue.size());
                stats.setTotalMemory(Runtime.getRuntime().totalMemory());
                stats.setFreeMemory(Runtime.getRuntime().freeMemory());
                return stats;
        }

        // Inherit comments from ErosServiceUI
        public long getCurrentQueueSize() throws RemoteException {
                return this.workQue.size();
        }

        // Inherit comments from ErosServiceUI
        public long getErrorQueueSize() throws RemoteException {
                return this.errorQue.size();
        }

        // Inherit comments from ErosServiceUI
        public long getRecievedCount() throws RemoteException {
                return this.logCount;
        }

        public ErrorModel getErrorRecords(Date from, Date to) throws RemoteException {
                try {
                        return store.getRecords(from, to);
                } catch (Exception ex) {
                        logger.log(Level.SEVERE, ex.getMessage(), ex);
                        throw new RemoteException(ex.getMessage(), ex);
                }
        }

        /**
         * Controls the status of the server. The server will not accept any
         * incoming log requests when processing is set to false.
         * 
         * @param processing
         *                The new processing status for the service.
         */
        public void setProcessing(boolean processing) {
                logger.fine("About to set processing to = " + processing);
                synchronized (this.processing) {
                        this.processing = new Boolean(processing);
                }
                logger.fine("Change to processing completed.");
        }

        /**
         * Performs any clean-up tasks before the service is shutdown.
         */
        private class ShutdownThread
                        extends
                        Thread {
                public ShutdownThread() {
                        super("Shutdown Thread");
                }

                /**
                 * Performs any clean-up tasks
                 */
                public void run() {
                        System.out.println("Shutting down service...");
                        try {
                                log.snapshot();
                                store.close();
                                DefaultExporterManager.getManager().relinquishAll();
                                manager.terminate();
                                discoveryManager.terminate();
                        } catch (Exception exc) {
                        }
                        System.out.println("Shutdown complete.");
                }
        }

        /**
         * Class to provide reliable log functionality for the Eros service.
         * Ensures all the required data is restored between startups
         */
        private class ErosLogHandler
                        extends
                        LogHandler {
                /**
                 * Create the LogHandler
                 */
                public ErosLogHandler() {
                }

                /**
                 * Writes all of the data that should be persisted to the
                 * output.
                 * 
                 * @param os
                 *                The stream to which the data should be written
                 *                to.
                 * @throws Exception
                 *                 Any problems writing the data.
                 */
                public void snapshot(OutputStream os) throws Exception {
                        ObjectOutputStream oos = new ObjectOutputStream(os);
                        oos.writeObject(servID);
                        oos.writeObject(groups);
                        oos.writeObject(marshalAttributes(attribs));
                        oos.writeObject(workQue);
                        oos.writeObject(errorQue);
                        oos.writeObject(stats);
                }

                /**
                 * Reads all the data from the stream provided and restores the
                 * state of the data for this object
                 * 
                 * 
                 * param is
                 * 
                 * he stream from which all the required data should be read
                 */
                public void recover(InputStream is) throws Exception {
                        ObjectInputStream ois = new ObjectInputStream(is);
                        servID = (ServiceID) ois.readObject();
                        groups = (String[]) ois.readObject();
                        MarshalledObject[] marshalledObjs = (MarshalledObject[]) ois.readObject();
                        attribs = unmarshalAttributes(marshalledObjs);
                        workQue = (Vector) ois.readObject();
                        errorQue = (Vector) ois.readObject();
                        stats = (ErosServiceStats) ois.readObject();
                }

                /**
                 * Takes the object supplied and applies any changes to the
                 * internal data.
                 * 
                 * @param obj
                 *                An instance of <code>ErosLogRecord</code>
                 *                that contains any changes to the data.
                 */
                public void applyUpdate(Object obj) {
                        ((ErosLogRecord) obj).apply(ErosServiceImpl.this);
                }
        }

        /**
         * Defines the operations that should be supported by any update records
         */
        private static interface ErosLogRecord extends java.io.Serializable {
                /**
                 * Perform any required updates to the service provided
                 * 
                 * 
                 * ram erosService
                 * 
                 * instance of the service that updates should be applied
                 * 
                 * 
                 */
                void apply(ErosServiceImpl erosService);
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Contains the changes to the set of groups this service is joined.
         */
        private static class ErosGroupsChangedRecord implements ErosLogRecord {
                String[] groups;

                public void apply(ErosServiceImpl erosService) {
                        erosService.groups = groups;
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Contains the changes to the set of attributes for this service.
         */
        private static class ErosAttribsChangedRecord implements ErosLogRecord {
                MarshalledObject[] attribs;

                public void apply(ErosServiceImpl erosService) {
                        erosService.attribs = erosService.unmarshalAttributes(attribs);
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Adds a <code>LogDetail</code> object to the work queue.
         */
        private static class ErosWorkItemAdded implements ErosLogRecord {
                LogDetail logDetail;

                public void apply(ErosServiceImpl erosService) {
                        erosService.workQue.add(logDetail);
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Removes a <code>LogDetail</code> object from the work queue.
         */
        private static class ErosWorkItemRemoved implements ErosLogRecord {
                LogDetail logDetail;

                public void apply(ErosServiceImpl erosService) {
                        erosService.workQue.remove(logDetail);
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Adds a <code>LogDetail</code> object to the error queue.
         */
        private static class ErosErrorItemAdded implements ErosLogRecord {
                String filePath;

                public void apply(ErosServiceImpl erosService) {
                        erosService.errorQue.add(filePath);
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Removes a <code>LogDetail</code> object from the error queue.
         */
        private static class ErosErrorItemRemoved implements ErosLogRecord {
                String filePath;

                public void apply(ErosServiceImpl erosService) {
                        if (filePath != null)
                                erosService.errorQue.remove(filePath);
                }
        }

        /**
         * Record written as an update record using the <code>ReliableLog</code>.
         * Contains the statistics for the service.
         */
        private static class ErosStatsChangeRecord implements ErosLogRecord {
                ErosServiceStats stats;

                public void apply(ErosServiceImpl erosService) {
                        erosService.stats = stats;
                }
        }

        /**
         * Listener class for notification of a <code>ServiceID</code>. Used
         * only when no <code>ServiceID</code> is found in the persistant
         * store.
         */
        private class ErosServIDListener implements ServiceIDListener {
                /**
                 * Persists the <code>ServiceID</code> provided using the
                 * reliable log
                 */
                public void serviceIDNotify(ServiceID servID) {
                        logger.fine("ServiceID notified.");
                        ErosServiceImpl.this.servID = servID;
                        try {
                                synchronized (log) {
                                        log.snapshot();
                                }
                        } catch (Exception exc) {
                                exc.printStackTrace();
                        }
                }
        }

        /**
         * Marshals each element of the <code>Entry[]</code> array parameter.
         * This method is <code>static</code> so that it may called from the
         * <code>static</code> <code>LogRecord</code> classes when a set of
         * attributes is being logged to persistent storage.
         * 
         * 
         * ram attrs
         * 
         * de>Entry[]</code> array consisting of the attributes to
         * 
         * shal
         * 
         * @return array of <code>MarshalledObject[]</code>, where each
         *         element corresponds to an attribute in marshalled form
         */
        private static MarshalledObject[] marshalAttributes(Entry[] attrs) {
                if (attrs == null)
                        return new MarshalledObject[0];
                java.util.ArrayList marshalledAttrs = new java.util.ArrayList();
                for (int i = 0; i < attrs.length; i++) {
                        /*
                         * Do not let an attribute problem prevent the service
                         * from continuing to operate
                         */
                        try {
                                marshalledAttrs.add(new MarshalledObject(attrs[i]));
                        } catch (Throwable e) {
                                System.out.println("Error while marshalling attribute[" + i + "] (" + attrs[i] + "): " + e.toString());
                        }
                }
                return ((MarshalledObject[]) (marshalledAttrs.toArray(new MarshalledObject[marshalledAttrs.size()])));
        } // end marshalAttributes

        /**
         * Unmarshals each element of the <code>MarshalledObject[]</code>
         * array parameter. This method is <code>static</code> so that it may
         * called from the <code>static</code> <code>LogRecord</code>
         * classes when a set of attributes is being recovered from persistent
         * storage.
         * 
         * 
         * ram marshalledAttrs
         * 
         * de>MarshalledObject[]</code> array consisting of the
         * 
         * ributes to unmarshal
         * 
         * @return array of <code>Entry[]</code>, where each element
         *         corresponds to an attribute that was successfully
         *         unmarshalled
         */
        private static Entry[] unmarshalAttributes(MarshalledObject[] marshalledAttrs) {
                if (marshalledAttrs == null)
                        return null;
                java.util.ArrayList attrs = new java.util.ArrayList();
                for (int i = 0; i < marshalledAttrs.length; i++) {
                        /*
                         * Do not let an attribute problem prevent the service
                         * from continuing to operate
                         */
                        try {
                                attrs.add(marshalledAttrs[i].get());
                        } catch (Throwable e) {
                                System.out.println("Error while unmarshalling attribute[" + i + "]: " + e.toString());
                        }
                }
                return ((Entry[]) (attrs.toArray(new Entry[attrs.size()])));
        } // end unmarshalAttributes

        /**
         * Thread to process the work queue that holds the
         * <code>LogDetail</code> objects that have not yet been stored
         * centrally. For each item in the queue, an attempt is made to store
         * and if successful is removed from the queue. An update is made using
         * <code>ReliableLog</code> after each entry is removed.
         */
        private class WorkQueThread
                        extends
                        Thread {
                private long updateCount = 0;

                public WorkQueThread() {
                        super("WorkQueThread");
                        FileHandler fh;
                        try {
                                fh = new FileHandler("WorkQErrors");
                                logger.setLevel(Level.WARNING);
                                logger.addHandler(fh);
                        } catch (SecurityException e) {
                                // TODO Handle SecurityException
                                e.printStackTrace();
                        } catch (IOException e) {
                                // TODO Handle IOException
                                e.printStackTrace();
                        }

                }

                /**
                 * Processes each entry from the queue, when the processing
                 * status of the service is set to true.
                 * 
                 * @see ErosServiceImplOld#setProcessing(boolean)
                 */
                Logger logger = Logger.getLogger("WorkQueThreadLogger");

                public void run() {
                        ErosWorkItemRemoved workRecord = new ErosWorkItemRemoved();
                        ErosErrorItemAdded errorRecord = new ErosErrorItemAdded();
                        ErosStatsChangeRecord statsRecord = new ErosStatsChangeRecord();
                        while (processing.booleanValue()) {
                                try {
                                        synchronized (processing) {
                                                LogDetail logDetail = null;
                                                boolean errorLogging = false;
                                                long start = System.currentTimeMillis();
                                                File logFile = null;
                                                try {
                                                        if (workQue.size() > 0) {
                                                                try {
                                                                        logDetail = (LogDetail) workQue.get(0);
                                                                } catch (Exception exc) {
                                                                        // Ignore,
                                                                        // no
                                                                        // items
                                                                        // to
                                                                        // process
                                                                }
                                                                if (logDetail != null) {
                                                                        store.log(logDetail);
                                                                        workQue.remove(0);
                                                                        workerProcessedCount++;
                                                                        // Pass
                                                                        // to
                                                                        // the
                                                                        // helpdesk
                                                                        // store
                                                                        // for
                                                                        // logging
                                                                        // if
                                                                        // required.
                                                                        // helpdeskStore.log(logDetail);
                                                                        updateStats(logDetail);
                                                                }
                                                                logger.fine("Logged with central store.");
                                                        }
                                                } catch (Exception exc) {
                                                        logger.log(Level.WARNING, "Unable to log error centrally, saving to disk.", exc);
                                                        errorLogging = true;
                                                        boolean saved = false;
                                                        Exception lastExc = null;
                                                        for (int i = 0; i < 5 && !saved; i++) {
                                                                try {
                                                                        do {
                                                                                logFile = new File(STORE_DIR + File.separator + Math.random() + ".obj");
                                                                        } while (logFile.exists());
                                                                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(logFile));
                                                                        oos.writeObject(logDetail);
                                                                        oos.flush();
                                                                        oos.close();
                                                                        errorQue.add(logFile.getAbsolutePath());
                                                                        saved = true;
                                                                } catch (Exception ioExc) {
                                                                        lastExc = ioExc;
                                                                }
                                                        }
                                                        if (!saved) {
                                                                logger.log(Level.WARNING, "Unable to save log detail after failed log.\n" + logDetail.toString(), lastExc);
                                                        }
                                                        workQue.remove(0);
                                                }
                                                long logStart = System.currentTimeMillis();
                                                // If we have processed more
                                                // than the threshold take
                                                // a snapshot otherwise just an
                                                // update
                                                try {
                                                        if (logDetail != null) {
                                                                if (workerProcessedCount > UPDATE_THRESHOLD) {
                                                                        logger.fine("About to take reliable log snapshot");
                                                                        synchronized (log) {
                                                                                log.snapshot();
                                                                                workerProcessedCount = 0;
                                                                                logger.info("ReliableLog Snapshot created, " + "number left to process = " + workQue.size());
                                                                        }
                                                                } else {
                                                                        logger.fine("About to take reliable log update");
                                                                        synchronized (log) {
                                                                                workRecord.logDetail = logDetail;
                                                                                log.update(workRecord, true);
                                                                                statsRecord.stats = stats;
                                                                                log.update(statsRecord, true);
                                                                                if (errorLogging) {
                                                                                        errorRecord.filePath = logFile.getAbsolutePath();
                                                                                        log.update(errorRecord, true);
                                                                                }
                                                                        }
                                                                }
                                                        }
                                                } catch (Exception logExc) {
                                                        logger.log(Level.WARNING, "Problem saving queue states using ReliableLog.", logExc);
                                                }
                                                // Need to account for the log
                                                // counter being reset to
                                                // zero.
                                                if (logCount < updateCount)
                                                        updateCount = logCount;
                                                // If update threshold reached
                                                // create a snapshot.
                                                if ((logCount - updateCount) > UPDATE_THRESHOLD) {
                                                        updateCount = logCount;
                                                        try {
                                                                logger.fine("Creating log snapshot.");
                                                                synchronized (log) {
                                                                        log.snapshot();
                                                                }
                                                                logger.info("ReliableLog Snapshot created, log count = " + updateCount);
                                                        } catch (Exception exc) {
                                                                logger.log(Level.WARNING, "Error creating snapshot", exc);
                                                        }
                                                }
                                                if (logDetail != null) {
                                                        long end = System.currentTimeMillis();
                                                        logger.fine("Worker time=" + (end - start) + "ms, reliable log took=" + (end - logStart) + "ms");
                                                }
                                        }
                                } catch (Exception exc) {
                                        logger.log(Level.WARNING, "Unexpected problem processing work items", exc);
                                }
                                if (workQue.size() == 0) {
                                        try {
                                                Thread.sleep(5000);
                                        } catch (Exception exc) {
                                                // Ignore
                                        }
                                }
                        }
                }
        }

        /**
         * Thread that processes all items from the error queue.
         */
        private class ErrorQueWorker
                        extends
                        Thread {
                public ErrorQueWorker() {
                        super("ErrorQueWorker");
                        // Check all error files on disk are present in the
                        // error queue.
                        File[] errorFiles = new File(STORE_DIR).listFiles();
                        int addedCount = 0;
                        for (int i = 0; i < errorFiles.length; i++) {
                                ErosErrorItemAdded errorRecord = new ErosErrorItemAdded();
                                File errorFile = errorFiles[i];
                                if (!errorQue.contains(errorFile.getAbsolutePath())) {
                                        errorQue.add(errorFile.getAbsolutePath());
                                        errorRecord.filePath = errorFile.getAbsolutePath();
                                        synchronized (log) {
                                                try {
                                                        log.update(errorRecord, true);
                                                } catch (IOException e) {
                                                        logger.log(Level.WARNING, "Failed adding additional error file " + " to logs, will still be processed.", e);
                                                }
                                        }
                                        addedCount++;
                                }
                        }
                        logger.info("Number of additional error objects added to queue " + "from disk - " + addedCount);
                }

                public void run() {
                        ErosErrorItemRemoved errorRecord = new ErosErrorItemRemoved();
                        ErosStatsChangeRecord statsRecord = new ErosStatsChangeRecord();
                        while (processing.booleanValue()) {
                                try {
                                        boolean connectErrorLogged = false;
                                        synchronized (processing) {
                                                Iterator iter = errorQue.iterator();
                                                LogDetail logDetail = null;
                                                while (iter.hasNext()) {
                                                        try {
                                                                File logFile = null;
                                                                Object item = iter.next();
                                                                logFile = new File((String) item);
                                                                if (logFile.exists()) {
                                                                        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(logFile));
                                                                        logDetail = (LogDetail) ois.readObject();
                                                                        ois.close();
                                                                } else {
                                                                        iter.remove();
                                                                }
                                                                if (logDetail != null) {
                                                                        store.log(logDetail);
                                                                        iter.remove();
                                                                        workerProcessedCount++;
                                                                        updateStats(logDetail);
                                                                        statsRecord.stats = stats;
                                                                        if (logFile != null) {
                                                                                try {
                                                                                        logFile.delete();
                                                                                } catch (Exception e) {
                                                                                        logger.log(Level.WARNING, "Unable to delete log file - " + logFile.getAbsolutePath(), e);
                                                                                }
                                                                        }
                                                                        logger.info("Logged successfully from error que");
                                                                        try {
                                                                                if (workerProcessedCount > UPDATE_THRESHOLD) {
                                                                                        synchronized (log) {
                                                                                                log.snapshot();
                                                                                        }
                                                                                        workerProcessedCount = 0;
                                                                                        logger.info("ReliableLog Snapshot created, " + "errors left to process = " + errorQue.size());
                                                                                } else {
                                                                                        synchronized (log) {
                                                                                                errorRecord.filePath = logFile.getAbsolutePath();
                                                                                                log.update(errorRecord, true);
                                                                                                log.update(statsRecord, true);
                                                                                        }
                                                                                }
                                                                        } catch (Exception logExc) {
                                                                                logger.log(Level.WARNING, "Problem using ReliableLog.", logExc);
                                                                        }
                                                                }
                                                        } catch (StoreUnavailableException storeExc) {
                                                                if (!connectErrorLogged) {
                                                                        logger.info("Still unable to log centrally - " + storeExc.getMessage());
                                                                        connectErrorLogged = true;
                                                                }
                                                        }
                                                        logDetail = null;
                                                }
                                        }
                                } catch (Exception e) {
                                        logger.log(Level.WARNING, "Unexpected error processing error queue", e);
                                }
                                try {
                                        Thread.sleep(2 * 60 * 1000);
                                } catch (Exception exc) {
                                        logger.log(Level.INFO, "Error in sleeping", exc);
                                }
                        }
                }

        }

        private void updateStats(LogDetail logDetail) throws Exception {
                InstanceDetail detail = null;
                Long instID = new Long(logDetail.getInstanceIdentifier());

                if (instanceMap.containsKey(instID)) {
                        detail = (InstanceDetail) instanceMap.get(instID);
                } else {
                      
                                detail = store.loadInstanceRecord(logDetail.getInstanceIdentifier());
                                instanceMap.put(instID, detail);
                        
                }
                stats.update(detail, logDetail);
        }

        //
        // /**
        // * @deprecated Should no longer be used, the activation start class
        // * <code>StartEros</code> should be used instead.
        // * @param args
        // */
        // public static void main(String[] args) {
        // try {
        // ErosServiceImpl eros = null;
        // if(erosShutDown == null) {
        // if( args.length == 2 ) {
        // String[] jiniGroup = new String[]{args[0]};
        // java.io.File logDir = new java.io.File(args[1]);
        // eros = new ErosServiceImpl(jiniGroup, logDir.getAbsolutePath());
        // } else {
        // System.out.println("The start format for Eros is : ");
        // System.out.println("org.jini.projects.eros.ErosServiceImpl <group>
        // <logdir>");
        // }
        // synchronized(eros) {
        // eros.wait(0);
        // }
        // } else {
        // erosShutDown.run();
        // System.exit(0);
        // }
        // } catch (Exception exc) {
        // exc.printStackTrace();
        // }
        // }
        public static void main(String args[]) {
                try {
                        System.setSecurityManager(new RMISecurityManager());
                        DefaultExporterManager.getManager("default", "config/exportmgr.config");
                        ErosServiceImpl app = new ErosServiceImpl(args);
                } catch (Exception e) {
                        // URGENT Handle ConfigurationException
                        e.printStackTrace();
                }
        }
}
