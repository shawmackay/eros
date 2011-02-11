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
package org.jini.projects.eros.admin;

import net.jini.core.entry.Entry;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.JoinManager;

import org.jini.projects.eros.ErosServiceImpl;

import java.rmi.Remote;
import java.rmi.RemoteException;

import java.rmi.activation.ActivationID;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * Provides all the administration functions for the service.
 */
public class ErosAdminImpl  implements ErosAdmin, Remote {

    private transient ErosServiceImpl eros = null;
    private transient JoinManager jm = null;
    private transient LookupDiscoveryManager ldm = null;
    private transient DestroyThread destroyThread = null;
    private transient Logger logger = null;
    private transient ActivationID activationID = null;
    
    /**
     * Constructor.
     * @param jm The JoinManager currently being used.  This is required to
     *          forward JoinAdmin requests to.
     * @param ldm The LookupDiscoveryManager being used.  This is required to
     *          forware JoinAdmin requests to.
     * @throws RemoteException 
     */
    public ErosAdminImpl(ActivationID actID, ErosServiceImpl eros, JoinManager jm,
                            LookupDiscoveryManager ldm) throws RemoteException {
        this.activationID = actID;
        this.eros = eros;
        this.jm = jm;
        this.ldm = ldm;
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    //Inherit comments
    public Entry[] getLookupAttributes() throws RemoteException {
        return jm.getAttributes();
    }

    //Inherit comments
    public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
        jm.addAttributes(attrSets, true);
        eros.setAttributes(this.getLookupAttributes());
    }

    //Inherit comments
    public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
        jm.modifyAttributes(attrSetTemplates, attrSets, true);
        eros.setAttributes(this.getLookupAttributes());
    }

    //Inherit comments
    public String[] getLookupGroups() throws RemoteException {
        return ldm.getGroups();
    }

    //Inherit comments
    public void addLookupGroups(String[] groups) throws RemoteException {
        try {
            ldm.addGroups(groups);
        } catch (IOException ioExc) {
            throw new RemoteException("", ioExc);
        }
        eros.setGroups(this.getLookupGroups());
    }

    //Inherit comments
    public void removeLookupGroups(String[] groups) throws RemoteException {
        ldm.removeGroups(groups);
        eros.setGroups(this.getLookupGroups());
    }

    //Inherit comments
    public void setLookupGroups(String[] groups) throws RemoteException {
        try {
            ldm.setGroups(groups);
        } catch (IOException ioExc) {
            throw new RemoteException("Exception settting groups.", ioExc);
        }
        eros.setGroups(this.getLookupGroups());
    }

    //Inherit comments
    public LookupLocator[] getLookupLocators() throws RemoteException {
        return ldm.getLocators();
    }

    //Inherit comments
    public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
        ldm.addLocators(locators);
    }

    //Inherit comments
    public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
        ldm.removeLocators(locators);
    }

    //Inherit comments
    public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
        ldm.setLocators(locators);
    }

    //Inherit comments
    public void destroy() throws RemoteException {
        if( destroyThread == null )
            destroyThread = new DestroyThread();
        destroyThread.start();
    }

    /**
     * Provides the required shutdown functionality.
     * Calls terminate to the relevant managers and removes all the
     * persistant logs for the service.
     */
    class DestroyThread extends Thread {
        public DestroyThread() {
            super("ErosDestroyThread");
            setDaemon(false);
            eros.setProcessing(false);
            logger.fine("Call to service to stop processing made");
        }

        public void run() {
            try {
                jm.terminate();
                ldm.terminate();
                eros.removeLogs();
            } catch (Exception exc) {
                System.out.println("Error terminating helpers. - " + exc.getMessage());
            }

            
            System.out.println("Eros - Destroy Complete.");
        }
    }

}
