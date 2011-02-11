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
 * eros : org.blarty.eros.constrainable
 * 
 * 
 * ErosProxy.java
 * Created on 15-Jan-2004
 * 
 * ErosProxy
 *
 */
package org.blarty.eros.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;


import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.id.Uuid;

import org.blarty.eros.admin.ErosAdmin;



/**
 * @author calum
 */
public class ErosAdminProxy implements ErosAdmin,  Serializable{

    private static final long serialVersionUID = 2L;
    transient Logger l = Logger.getLogger("eros.service");
    
    final ErosAdmin backend;
    final Uuid ID;
    
    
    final static class ConstrainableErosAdminProxy extends ErosAdminProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableErosAdminProxy(ErosAdmin server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new ErosAdminProxy.ConstrainableErosAdminProxy(backend, ID,
                    constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static ErosAdmin constrainServer(ErosAdmin server, MethodConstraints methodConstraints)
        {
            return (ErosAdmin)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }
    }
    
    
    public static ErosAdminProxy create(ErosAdmin server, Uuid id) {
        if (server instanceof RemoteMethodControl) {
            
            return new ErosAdminProxy.ConstrainableErosAdminProxy(server,  id, null);
        }
        else
            return new ErosAdminProxy(server,  id);
    }
    
	/**
	 * 
	 */
	private  ErosAdminProxy(ErosAdmin backend, Uuid id) {
		super();
		// URGENT Complete constructor stub for ErosProxy
        this.backend = backend;
        this.ID = id;
	}

	
	

	/**
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
		backend.addLookupAttributes(attrSets);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupGroups(String[] groups) throws RemoteException {
		backend.addLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.addLookupLocators(locators);
	}

	/**
	 * @throws java.rmi.RemoteException
	 */
	public void destroy() throws RemoteException {
		backend.destroy();
	}

	/* @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return backend.equals(obj);
	}

	/**
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public Entry[] getLookupAttributes() throws RemoteException {
		return backend.getLookupAttributes();
	}

	/**
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public String[] getLookupGroups() throws RemoteException {
		return backend.getLookupGroups();
	}

	/**
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public LookupLocator[] getLookupLocators() throws RemoteException {
		return backend.getLookupLocators();
	}

	/* @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return backend.hashCode();
	}

	/**
	 * @param attrSetTemplates
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
	public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
		backend.modifyLookupAttributes(attrSetTemplates, attrSets);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void removeLookupGroups(String[] groups) throws RemoteException {
		backend.removeLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.removeLookupLocators(locators);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void setLookupGroups(String[] groups) throws RemoteException {
		backend.setLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.setLookupLocators(locators);
	}

	/* @see java.lang.Object#toString()
	 */
	public String toString() {
		return backend.toString();
	}

}
