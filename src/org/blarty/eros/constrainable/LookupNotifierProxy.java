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
 * LookupNotifierProxy.java
 * Created on 19-Jan-2004
 * 
 * LookupNotifierProxy
 *
 */
package org.blarty.eros.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.id.Uuid;


/**
 * @author calum
 */
public class LookupNotifierProxy implements RemoteEventListener, Serializable{

     transient Logger l = Logger.getLogger("eros.monitor");
    
    final RemoteEventListener backend;
    final Uuid proxyID;
    
    
    
    final static class ConstrainableLookupNotifierProxy extends LookupNotifierProxy implements RemoteMethodControl{
        private static final long serialVersionUID = 4L;
        private ConstrainableLookupNotifierProxy(RemoteEventListener server, Uuid id, MethodConstraints methodConstraints) {
            super(constrainServer(server,methodConstraints), id);
            l.fine("Creating a secure proxy");
        }
        public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new LookupNotifierProxy.ConstrainableLookupNotifierProxy(backend, proxyID,
                    constraints);
        }

        
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


        private static RemoteEventListener constrainServer(RemoteEventListener server, MethodConstraints methodConstraints)
        {
            return (RemoteEventListener)
            ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }
    }
    
    
    public static LookupNotifierProxy create(RemoteEventListener server, Uuid id) {
        if (server instanceof RemoteMethodControl) {
            
            return new LookupNotifierProxy.ConstrainableLookupNotifierProxy(server,  id, null);
        }
        else
            return new LookupNotifierProxy(server,  id);
    }
    
    /**
     * 
     */
    private  LookupNotifierProxy(RemoteEventListener backend, Uuid id) {
        super(); 
        this.backend = backend;
        this.proxyID = id;
    }
	

	

	/**
	 * @param theEvent
	 * @throws net.jini.core.event.UnknownEventException
	 * @throws java.rmi.RemoteException
	 */
	public void notify(RemoteEvent theEvent) throws UnknownEventException, RemoteException {
		backend.notify(theEvent);
	}

}
