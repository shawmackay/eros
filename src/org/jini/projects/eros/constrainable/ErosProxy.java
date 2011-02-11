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
 * eros : org.jini.projects.eros.constrainable
 * 
 * 
 * ErosProxy.java
 * Created on 15-Jan-2004
 * 
 * ErosProxy
 *
 */
package org.jini.projects.eros.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.Uuid;

import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.ErosServiceStats;
import org.jini.projects.eros.InstanceDetail;
import org.jini.projects.eros.LogDetail;
import org.jini.projects.eros.ErosInterface;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;


/**
 * @author calum
 */
public class ErosProxy implements ErosInterface, Administrable,  Serializable{

    private static final long serialVersionUID = 2L;
    transient Logger l = Logger.getLogger("eros.service");
    
    final ErosInterface backend;
    final Uuid ID;
    
    
    final static class ConstrainableErosProxy extends ErosProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableErosProxy(ErosInterface server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new ErosProxy.ConstrainableErosProxy(backend, ID,
                    constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static ErosInterface constrainServer(ErosInterface server, MethodConstraints methodConstraints)
        {
            return (ErosInterface)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }
    }
    
    
    public static ErosProxy create(ErosInterface server, Uuid id) {
        if (server instanceof RemoteMethodControl) {
            
            return new ErosProxy.ConstrainableErosProxy(server,  id, null);
        }
        else
            return new ErosProxy(server,  id);
    }
    
	/**
	 * 
	 */
	private  ErosProxy(ErosInterface backend, Uuid id) {
		super();
		// URGENT Complete constructor stub for ErosProxy
        this.backend = backend;
        this.ID = id;
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
	public Object getAdmin() throws RemoteException {
		return backend.getAdmin();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public long getCurrentQueueSize() throws RemoteException {
		return backend.getCurrentQueueSize();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public long getErrorQueueSize() throws RemoteException {
		return backend.getErrorQueueSize();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public ErosLogger getLogger() throws RemoteException {
		return backend.getLogger();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public long getRecievedCount() throws RemoteException {
		return backend.getRecievedCount();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public ErosServiceStats getStats() throws RemoteException {
		return backend.getStats();
	}

	/* @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return backend.hashCode();
	}

	/**
	 * @param logDetail
	 * @throws RemoteException
	 */
	public void log(LogDetail logDetail) throws RemoteException {
		backend.log(logDetail);
	}

	/* @see java.lang.Object#toString()
	 */
	public String toString() {
		return backend.toString();
	}

        public long createInstanceRecord(InstanceDetail instanceDetail) throws RemoteException {
                // TODO Auto-generated method stub
                return backend.createInstanceRecord(instanceDetail);
        }

        public ErrorModel getErrorRecords(Date from, Date to) throws RemoteException {
                return backend.getErrorRecords(from, to);
        }
    
    

}
