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
 * ClientTest.java
 *
 * Created on 11 March 2002, 13:48
 */

package org.jini.projects.eros.client;

import java.rmi.*;

import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.Random;
import java.util.Vector;

import net.jini.core.lookup.*;
import net.jini.core.entry.*;
import net.jini.discovery.*;
import net.jini.lookup.*;
import net.jini.lookup.entry.*;

import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.ErosService;
import org.jini.projects.eros.LogLevel;
import org.jini.projects.eros.logging.ErosHandler;
import org.jini.projects.eros.logging.ErosHandlerImpl;

/**
 * Testing client for Eros. Simply locates an Eros service and logs a number of
 * dummy messages to the server.
 */
public class ErosClient
                implements
                DiscoveryListener {

        private LookupDiscoveryManager ldm = null;

        private String[] appNames = new String[] { "Athena","Isis", "WebApp", "Thor", "Osiris", "Themis", "Neon", "Zenith", "Wolf", "Odin", "Horus", "TestClient1", "TestClient2", "TestClient3", "TestClient4" };

        private int noLogs = 1;

        private boolean discovered = false;

        public ErosClient(String group, String count) throws Exception {
                if (System.getSecurityManager() == null)
                        System.setSecurityManager(new RMISecurityManager());
                if (count != null) {
                        noLogs = Integer.parseInt(count);
                }
                ldm = new LookupDiscoveryManager(new String[] { group }, null, this);
                System.out.println("Registered for discovery...");

        }

        public void discovered(DiscoveryEvent de) {
                Random random = new Random(System.currentTimeMillis());
                if (!discovered) {
                        try {
                                discovered = true;
                                System.out.println("Discovered.......");
                               // System.getProperties().put("org.jini.projects.eros.debug", "true");

                                ServiceRegistrar[] regs = de.getRegistrars();
                                for (int i = 0; i < regs.length; i++) {
                                        for (int j = 0; j < regs[i].getGroups().length; j++) {
                                                System.out.println(regs[j].getLocator().getHost());

                                                // Entry entries[] = new Entry[]
                                                // { new Name("ErosService") };
                                                ErosService ers = (ErosService) regs[i].lookup(new ServiceTemplate(null, new Class[] { ErosService.class }, null));
                                                if (ers != null) {
                                                        System.out.println("Eros service located");
                                                        for (int k = 0; k < noLogs; k++) {
                                                                String appName = appNames[random.nextInt(appNames.length)];
                                                                System.out.println("Intialising AppName to: " + appName);
                                                                System.out.println("Getting local logger from service.....");
                                                                long getLogger = System.currentTimeMillis();
                                                                ErosLogger erosLogger = ers.getLogger();
                                                                System.out.println("Local logger retrieved - " + (System.currentTimeMillis() - getLogger) + "ms");
                                                               
                                                                // String name =
                                                                // "TestClient"
                                                                // + new
                                                                // java.util.Random().nextInt();
                                                                String name = "TestClient";
                                                                System.out.println("Initialising eros.....");
                                                                erosLogger.initialise(appName);
                                                                
                                                                System.out.println("Initialisation complete.");

                                                                erosLogger.setPublishingLevel(Level.FINEST);

                                                                java.util.logging.Logger utilLogger = java.util.logging.Logger.getLogger("MyLogger");
                                                                utilLogger.setUseParentHandlers(false);
                                                                ErosHandler handler = (ErosHandler) erosLogger.getLoggingHandler();

                                                                // handler.setFileLevel(Level.WARNING);
                                                                // handler.setConsoleLevel(Level.FINE);
                                                                handler.setLevel(Level.FINE);

                                                                utilLogger.addHandler((Handler) handler);
                                                                utilLogger.setLevel(java.util.logging.Level.ALL);
                                                                

                                                                Logger l = Logger.getLogger(this.getClass().getName());
                                                                int type = random.nextInt(9);
                                                                try {
                                                                        switch (type) {
                                                                        case 0:
                                                                                int a = 0;
                                                                                int b = 2;
                                                                                System.out.println(b / a);
                                                                                break;
                                                                        case 1:
                                                                                String str = null;
                                                                                System.out.println(str.charAt(4));
                                                                                break;
                                                                        case 3:
                                                                                String str2 = "abc";
                                                                                System.out.println(str2.charAt(4));
                                                                                break;
                                                                        case 4:
                                                                                int nonumber = Integer.parseInt(new String("1.454tyr"));
                                                                        case 5:
                                                                                throw new ConnectException("* Can't connect");
                                                                        case 6:
                                                                                throw new AccessException("* Security Access");
                                                                        case 7:
                                                                                throw new IllegalMonitorStateException("* IMS occured");
                                                                        case 8:
                                                                                throw new RemoteException("* Remote Failure  ....");
                                                                        }
                                                                     
                                                                } catch (Exception e) {
                                                                        utilLogger.log(Level.SEVERE,"Ooops",e);
                                                                }
                                                                Thread.sleep(2000);
                                                                utilLogger.log(Level.INFO,"Hello this should bind together " + System.currentTimeMillis());
                                                        }

                                                        System.exit(0);
                                                }
                                        }
                                }
                        }

                        catch (Exception exc) {
                                exc.printStackTrace();
                        }
                        System.exit(1);
                }

        } /*
                 * public void discovered(DiscoveryEvent de) { try {
                 * System.out.println("Discovered.......");
                 * System.getProperties().put("org.jini.projects.eros.debug","true");
                 * System.getProperties().put("uk.co.cwa.debug","true");
                 * 
                 * ServiceRegistrar[] regs = de.getRegistrars(); for(int i=0; i <
                 * regs.length; i++) { for(int j=0; j <
                 * regs[i].getGroups().length; j++) {
                 * System.out.println(regs[i].getGroups()[j]); }
                 * 
                 * Entry entries[] = new Entry[] {new Name("ErosService")};
                 * ErosService ers = (ErosService) regs[i].lookup(new
                 * ServiceTemplate(null, null, entries)); if(ers != null) {
                 * System.out.println("Eros service located");
                 * 
                 * System.out.println("Getting local logger from service.....");
                 * ErosLogger erosLogger = ers.getLogger();
                 * System.out.println("Local logger retrieved.");
                 * 
                 * String name = "TestClient" + new
                 * java.util.Random().nextInt();
                 * System.out.println("Initialising eros.....");
                 * erosLogger.initialise(name);
                 * System.out.println("Initialisation complete.");
                 * 
                 * try { Thread.sleep(5000); } catch (Exception exc){}
                 * 
                 * //System.setProperty("user.name",name);
                 * //System.setProperty("os.name","SOLARIS");
                 * java.util.logging.Logger utilLogger =
                 * java.util.logging.Logger.getLogger("MyLogger"); ErosHandler
                 * handler = (ErosHandler) erosLogger.getLoggingHandler();
                 * 
                 * //handler.setFileLevel(Level.WARNING);
                 * //handler.setConsoleLevel(Level.FINE);
                 * handler.setLevel(Level.FINE);
                 * 
                 * utilLogger.addHandler((Handler)handler);
                 * utilLogger.setLevel(java.util.logging.Level.ALL);
                 * 
                 * for(int j=0; j<1; j++) { try { java.util.Vector v = null;
                 * v.add(""); } catch (Exception exc) { long start =
                 * System.currentTimeMillis(); utilLogger.fine("Fine message");
                 * utilLogger.info("Info message"); utilLogger.warning("Warning
                 * message..."); utilLogger.log(java.util.logging.Level.SEVERE,
                 * "Error in addition", exc); long end =
                 * System.currentTimeMillis(); //System.out.println("Error
                 * logged="+j+ " took=" + (end-start) + "ms"); } } try {
                 * //Thread.sleep(2 * 60 * 1000); } catch (Exception exc) {}
                 * 
                 * System.exit(1); } } } catch (Exception exc) {
                 * exc.printStackTrace(); System.exit(1); } System.exit(0); }
                 */

        public void discarded(DiscoveryEvent de) {

        }

        public static void main(String[] args) {
                try {
                        ErosClient ec = null;

                        if (args.length == 2)
                                ec = new ErosClient(args[0], args[1]);
                        else
                                ec = new ErosClient(args[0], null);

                        synchronized (ec) {
                                ec.wait(0);
                        }
                } catch (Exception exc) {
                        exc.printStackTrace();
                }
                System.exit(1);
        }
}
