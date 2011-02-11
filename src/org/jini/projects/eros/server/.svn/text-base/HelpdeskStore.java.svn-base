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
 * HelpdeskStore.java
 * 
 * Created on 06 February 2002, 13:52 @author Chris
 */

package org.jini.projects.eros.server;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import org.jini.projects.eros.*;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;

/**
 * Performs the forwarding of logs to the Helpdesk system. The required data is
 * extracted from the <code>LogDetail</code> supplied and sent to the Helpdesk
 * system.
 */
public class HelpdeskStore implements ErosBackendStore {
	private Logger logger = null;
	private boolean enabled = false;
	private String serverDir = null;

	/**
	 * Creates the HelpdeskStore and determines if this logging is enabled. This
	 * is determined from the boolean value of the System property
	 * <i>uk.co.cwa.eros.helpdesklog </i>.
	 */
	public HelpdeskStore() {
		this(null);
		logger = Logger.getLogger(this.getClass().getName());
	}

	/**
	 * Creates the HelpdeskStore and determines if this logging is enabled. This
	 * is determined from the boolean value of the System property
	 * <i>uk.co.cwa.eros.helpdesklog </i>.
	 * 
	 * @param serverDir
	 *                   The directory to write files to for the helpdesk system.
	 */
	public HelpdeskStore(String serverDir) {
		enabled = Boolean.getBoolean("org.jini.projects.eros.helpdesklog");
		this.serverDir = serverDir;
	}

	/**
	 * Extracts the log details and generates the required input to the helpdesk
	 * system.
	 * 
	 * @param logDetail
	 *                   The logging detail to forward to the Helpdesk store.
	 * @return The success of the attempt to forward to the helpdesk system.
	 */
	public void log(LogDetail logDetail) throws Exception {
		boolean success = true;
		//Add in your logging to your helpdesk system here
	}

	

	/*
	 * @see org.jini.projects.eros.server.ErosBackendStore#close()
	 */
	public void close() throws Exception {
		// TODO Complete method stub for close
	}

	/*
	 * @see org.jini.projects.eros.server.ErosBackendStore#initialise(java.util.logging.Logger,
	 *           java.util.Map)
	 */
	public void initialise(Logger toConnectTo, Map parameters) throws Exception {
		// TODO Complete method stub for initialise
	}

        public long createInstanceRecord(InstanceDetail instanceDetail) throws Exception {
                // TODO Auto-generated method stub
                return 0;
        }

        public ErrorModel getRecords(Date from, Date to) throws Exception {
                // TODO Auto-generated method stub
                return null;
        }

        public InstanceDetail loadInstanceRecord(long id) throws Exception {
                // TODO Auto-generated method stub
                return null;
        }
}
