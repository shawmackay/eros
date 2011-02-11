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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.event.EventRegistration;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseRenewalManager;
import java.rmi.MarshalledObject;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceEvent;
import net.jini.core.lookup.ServiceID;
import org.jini.projects.eros.constrainable.LookupNotifierProxy;

/**
 * Client side class used to discover <code>ErosLogServer</code> instances, to
 * ensure whenever a server becomes available logs will be sent.
 * <p>
 * This class ensures that each time a call is made to log, the status of the
 * available server is checked, and if nessecary discovery of a new instance of
 * the service is initiated.
 */
public class ErosServiceMonitor implements DiscoveryListener,Serializable {
	
	private static final long serialVersionUID = 78797234234L;
	private LookupDiscoveryManager ldm = null;
	private String[] groups = null;
	private boolean serverAvailable = false;
	private ServiceID currentServerID = null;
	private ErosLogServer currentServer = null;
	private boolean debug = false;
	private int count = 0;
	private RemoteEventListener thisExported;
    private RemoteEventListener listener;
	private Exporter exporter;
	private Remote exported; 

	/**
	 * Instantiates the object.
	 */
	private ErosServiceMonitor() throws RemoteException {
		debug = Boolean.getBoolean("org.jini.projects.eros.debug");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new java.rmi.RMISecurityManager());
	}

	/**
	 * Creates the service monitor without an initial server instance to log to.
	 * Relies on the monitor to discover a server before logging can
	 * successfully take place.
	 * 
	 * @param groups
	 *                   The Jini groups in which the monitor should discover servers.
	 * @throws RemoteException
	 *                   Any problems in setting up the object for processing.
	 */
	public ErosServiceMonitor(String[] groups) throws RemoteException {
		this();
		this.groups = groups;
		startDiscovery();
	}

	/**
	 * Creates a instance of the service monitor and provides a server that
	 * should be used initially to log with.
	 * 
	 * @param logServer
	 *                   This server will be used until it is removed from the
	 *                   federation or an exception is generated while trying to log
	 *                   which will force rediscovery of a server.
	 * @param servID
	 *                   The ServiceID for the above log server service. Is required to
	 *                   monitor joining/leaving services.
	 * @param groups
	 *                   The Jini groups in which the monitor should discover servers.
	 * @throws RemoteException
	 *                   Any problems in setting the object up for processing.
	 */
	public ErosServiceMonitor(ErosLogServer logServer, ServiceID servID, String[] groups) throws RemoteException {
		this();
		this.groups = groups;
		if (debug)
			System.out.println("EROS - Setting current server to passed " + "server, groups = " + this.getGroups(this.groups));
		this.updateCurrentServer(logServer, servID, true);
		startDiscovery();
	}

	/**
	 * Starts the discovery of new servers.
	 */
	private void startDiscovery() throws RemoteException {
		try {
			if (debug){
				System.out.println("EROS - About to start discovery");
                for (int i=0;i<groups.length;i++)
                    System.out.println("\t" + groups[i]);
            }
			if (ldm != null) {
				ldm.terminate();
			}
			ldm = new LookupDiscoveryManager(this.groups, null, this);
		} catch (java.io.IOException ioExc) {
			throw new RemoteException("Unable to create LookupDiscoveryManager", ioExc);
		}
	}

	public class LookupNotifier implements RemoteEventListener {
		/**
		 * Notifies the monitor that a change has occurred with a service
		 * registered with the lookup service. If the server that is currently
		 * being used is removed then the discovery process is restarted to find
		 * a new server. If a new server is added, and the monitor does not have
		 * a current server the new server is used.
		 * 
		 * @param event
		 *                   The event that contains the service and the change type
		 */
		public synchronized void notify(RemoteEvent event) throws RemoteException {
			if (debug)
				System.out.println("EROS - ErosServiceMonitor - notified");
			try {
				ServiceEvent se = (ServiceEvent) event;
				if (se.getTransition() == ServiceRegistrar.TRANSITION_MATCH_NOMATCH) {
					//If service we have as current server then remove.
					if (se.getServiceID().equals(currentServerID)) {
						if (debug)
							System.out.println("EROS - ErosServiceMonitor, Current service removed.");
						updateCurrentServer(null, null, false);
						restartDiscovery();
					}
				} else if (se.getTransition() == ServiceRegistrar.TRANSITION_NOMATCH_MATCH) {
					//Only update the current server if we do not already have
					// one.
					if (se.getServiceItem() != null && se.getServiceItem().service != null && !isServerAvailable()) {
						if (debug)
							System.out.println("EROS - ErosServiceMonitor, Current service updated");
						updateCurrentServer((ErosLogServer) se.getServiceItem().service, se.getServiceID(), true);
					}
				}
			} catch (Exception exc) {
				if (debug)
					exc.printStackTrace();
			}
		}
	}

	/**
	 * Converts the String[] supplied into a single String containing the values
	 * separated by commas.
	 * 
	 * @param grps
	 *                   The String[] to convert to a single String
	 * @return The values separated by commas
	 */
	public static String getGroups(String[] grps) {
		if (grps != null) {
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < grps.length; j++) {
				sb.append(grps[j]);
				sb.append(",");
			}
			return sb.toString();
		} else {
			return "null";
		}
	}

	/**
	 * Registers with each Registrar provided in the discovery event for
	 * notification about ErosService changes. If a server is not currently
	 * available, it will also query each registrar for an available server.
	 */
	public synchronized void discovered(DiscoveryEvent e) {
		if (debug)
			System.out.println("EROS - ErosServiceMonitor, discovered.");
		ServiceRegistrar srs[] = e.getRegistrars();
		for (int i = 0; i < srs.length; i++) {
			try {
				ServiceRegistrar sr = srs[i];
				String[] grps = sr.getGroups();
				if (debug) {
					System.out.println("EROS - ServiceRegistrar groups = " + this.getGroups(grps));
					System.out.println("EROS - Registrar = " + sr.getLocator().getHost() + ":" + sr.getLocator().getPort());
				}
				Class[] classes = new Class[]{Class.forName("org.jini.projects.eros.ErosLogServer")};
				//See if I've already set up an exporter
				if (thisExported == null) {
                    listener = new LookupNotifier();
					exporter = new BasicJeriExporter(TcpServerEndpoint.getInstance(0), new BasicILFactory());
					exported = exporter.export(listener);
					thisExported = LookupNotifierProxy.create((RemoteEventListener) exported, UuidFactory.generate());
				}
				//Register for event notification
                
				EventRegistration er = sr.notify(new ServiceTemplate(null, classes, null), sr.TRANSITION_NOMATCH_MATCH | sr.TRANSITION_MATCH_NOMATCH, (RemoteEventListener) exported, new MarshalledObject(sr), Lease.FOREVER);
				new LeaseRenewalManager(er.getLease(), Lease.FOREVER, null);
				//Now try & lookup a current ErosLogServer with this registrar
				//only if a current server is not available.
				if (!this.isServerAvailable()) {
					ServiceMatches sm = sr.lookup(new ServiceTemplate(null, classes, null), 1);
					if (sm.items != null && sm.items.length > 0) {
						for (int j = 0; j < sm.items.length; j++) {
							try {
								ServiceItem si = sm.items[j];
								//Only update the current server if we do not
								// have one already.
								if (si != null && si.service != null && !this.isServerAvailable()) {
									if (debug) {
										System.out.println("EROS - ErosServiceMonitor, Found eros server");
										System.out.println("EROS - From registrar @ " + sr.getLocator().getHost());
										System.out.println("EROS - ServiceID = " + si.serviceID.toString());
										System.out.println("EROS - Service class = " + si.service.getClass());
									}
									ErosService erosService = (ErosService) si.service;
									this.updateCurrentServer((ErosLogServer) si.service, si.serviceID, true);
								}
							} catch (Exception exc) {
								if (debug)
									System.out.println("EROS - Exception examining ServiceItem");
							}
						}
					} else {
						if (debug)
							System.out.println("EROS - No service matches found!!!!!!!!!");
					}
				}
			} catch (Exception exc) {
				if (debug)
					exc.printStackTrace();
			}
		}
	}

	public void discarded(DiscoveryEvent event) {
	}

	/**
	 * Provides the current server that should be used to log with.
	 * 
	 * @return The available server that should be used to log with, or null if
	 *              no server is currently available.
	 */
	public ErosLogServer getCurrentServer() {
		return this.currentServer;
	}

	/**
	 * Used to update the server that should be returned to any clients. Should
	 * also be used to indicate that a server is no longer available using the
	 * boolean parameter.
	 * 
	 * @param logServer
	 *                   The server instance that should be made the current server
	 * @param servID
	 *                   The ServiceID for the service to make current
	 * @param available
	 *                   Is there a current server available
	 */
	private synchronized void updateCurrentServer(ErosLogServer logServer, ServiceID servID, boolean available) {
		if (debug)
			System.out.println("EROS - Server being set to available = " + available);
		this.currentServer = logServer;
		this.currentServerID = servID;
		this.serverAvailable = available;
	}

	/**
	 * Starts the discovery process
	 */
	private void restartDiscovery() {
		try {
			startDiscovery();
		} catch (Exception exc) {
			if (debug)
				exc.printStackTrace();
		}
	}

	/**
	 * Indicates if a server is currently available
	 */
	private synchronized boolean isServerAvailable() {
		return this.serverAvailable;
	}
    
    public long logInstanceInfo(InstanceDetail instanceDetail){
           long successInstID= -1;
           if(isServerAvailable())
                try {
                        successInstID = this.currentServer.createInstanceRecord(instanceDetail);
                } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();                        
                }
           return successInstID;
    }
    

	/**
	 * Logs the detail with the current server that is available. Any exceptions
	 * thrown while logging are caught and trigger rediscovery of a new server.
	 * 
	 * @param logDetail
	 *                   The detail that should be logged with the server.
	 * @return Indicates if logging with the server was successful
	 */
	public boolean log(LogDetail logDetail) {
		boolean success = false;
		if (isServerAvailable()) {
			if (debug)
				System.out.println("EROS - Trying current server.");
			try {
				this.currentServer.log(logDetail);
				count++;
				if (debug)
					System.out.println("EROS - Logged successfully, number - " + count);
				success = true;
			} catch (Exception exc) {
				if (debug)
					exc.printStackTrace();
				updateCurrentServer(null, null, false);
				try {
					startDiscovery();
				} catch (Exception discoExc) {
					if (debug)
						discoExc.printStackTrace();
				}
			}
		} else {
			if (debug)
				System.out.println("EROS - No server currently available");
		}
		return success;
	}

	/**
	 * Terminates all processing by the service monitor
	 */
	public void terminate() {
		if (ldm != null)
			ldm.terminate();
	}
}
