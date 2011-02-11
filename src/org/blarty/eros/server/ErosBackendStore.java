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
 * eros.jini.org : org.blarty.eros.server
 * 
 * 
 * ErosBackendStore.java
 * Created on 15-Apr-2004
 * 
 * ErosBackendStore
 *
 */

package org.blarty.eros.server;

import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import org.blarty.eros.InstanceDetail;
import org.blarty.eros.LogDetail;
import org.blarty.eros.ui.erroranalyser.model.ErrorModel;

/**
 * @author calum
 */
public interface ErosBackendStore { 
	/**
	 * Extracts the log details and generates the required input 
	 * @param logDetail The logging detail to forward to the backend store
	 * @return The success of the attempt to forward to the backend store
	 */
	public abstract void log(LogDetail logDetail) throws Exception;
    
    /**
     * Creates an instance record in the backend store, for subsequent loggings to be against
     * @param instanceDetail
     * @return 
     * @throws Exception
     */
    public long createInstanceRecord(InstanceDetail instanceDetail) throws Exception;
    
    
    public InstanceDetail loadInstanceRecord(long id) throws Exception;
    /** Inititalises the store and connects an implementation specific handler to the provided Logger
     * 
     * @param toConnectTo the Logger to connect the Bakcend Handler to.
     * @throws Exception
     */ 
    public abstract void initialise(Logger toConnectTo, Map parameters) throws Exception;
    
    public abstract void close() throws Exception;
    
    public ErrorModel getRecords(Date from, Date to) throws Exception;
}
