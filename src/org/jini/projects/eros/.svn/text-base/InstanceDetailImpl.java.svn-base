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

package org.jini.projects.eros;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InstanceDetailImpl implements InstanceDetail {
        private String applicationName;
        private String ipAddress;
        private String erosGroups;
        private String osName;
        private String osVersion;
        private String osUser;
        private long instanceID;
        String jvmVersion;
        
        public InstanceDetailImpl() throws Exception{
                InetAddress localHost = InetAddress.getLocalHost();
               String  hostAddress = localHost.getHostAddress();
                String hostName = localHost.getHostName();
                ipAddress = hostName + "(" + hostAddress +")";
                //Try and get the system properties required
                osName = System.getProperty("os.name") + "; " + 
                     System.getProperty("os.arch");
                osVersion=
                     System.getProperty("os.version");
                
                jvmVersion = System.getProperty("java.version");
                osUser = System.getProperty("user.name");

        }
        
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getApplicationName()
         */
        public String getApplicationName() {
                return applicationName;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setApplicationName(java.lang.String)
         */
        public void setApplicationName(String applicationName) {
                this.applicationName = applicationName;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getErosGroups()
         */
        public String getErosGroups() {
                return erosGroups;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setErosGroups(java.lang.String)
         */
        public void setErosGroups(String erosGroups) {
                this.erosGroups = erosGroups;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getIpAddress()
         */
        public String getIpAddress() {
                return ipAddress;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setIpAddress(java.lang.String)
         */
        public void setIpAddress(String ipAddress) {
                this.ipAddress = ipAddress;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getOsName()
         */
        public String getOsName() {
                return osName;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setOsName(java.lang.String)
         */
        public void setOsName(String osName) {
                this.osName = osName;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getOsUser()
         */
        public String getOsUser() {
                return osUser;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setOsUser(java.lang.String)
         */
        public void setOsUser(String osUser) {
                this.osUser = osUser;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#getOsVersion()
         */
        public String getOsVersion() {
                return osVersion;
        }
        /* (non-Javadoc)
         * @see org.jini.projects.eros.InstanceDetail#setOsVersion(java.lang.String)
         */
        public void setOsVersion(String osVersion) {
                this.osVersion = osVersion;
        }

        public String getJvmVersion() {
                return jvmVersion;
        }

        public void setJvmVersion(String jvmVersion) {
                this.jvmVersion = jvmVersion;
        }

        public void setInstanceIdentifier(long id) {
                // TODO Auto-generated method stub
                this.instanceID = id;
        }

        public long getInstanceIdentifier() {
                // TODO Auto-generated method stub
                return instanceID;
        }
}
