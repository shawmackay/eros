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
 * eros : org.blarty.eros
 * 
 * 
 * ErosServiceMonitorIntf.java
 * Created on 19-Jan-2004
 * 
 * ErosServiceMonitorIntf
 *
 */
package org.blarty.eros;

import net.jini.core.event.RemoteEventListener;
import net.jini.discovery.DiscoveryListener;

/**
 * @author calum
 */
public interface ErosServiceMonitorIntf extends RemoteEventListener{
    public boolean log(LogDetail logDetail);
}
