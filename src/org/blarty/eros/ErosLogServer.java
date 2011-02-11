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
package org.blarty.eros;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The interface that defines a server that is willing to
 * accept logging requests and will process them on behalf of
 * the client.
 */
public interface ErosLogServer extends Remote {

    /** 
     * Accepts a logging object and should guarantee that it
     * will be processed.  The log can either be processed
     * synchronously or queued.
     * @param logDetail The detail of the log that should be stored.
     * @throws RemoteException Any errors processing or queueing the log that results
     *              in the detail being lost from the server.
     */    
    void log(LogDetail logDetail) throws RemoteException ;

    /**
     * Creates a log Instance record for an application - one of these is needed 
     * for every application that uses Eros
     * @param instanceDetail DTO containing information about the host process
     * @return Instance ID to attach to every log record
     * @throws RemoteException Any errors thrown in the server that are not caught. 
     */
    
    long  createInstanceRecord(InstanceDetail instanceDetail) throws RemoteException;
    
}
