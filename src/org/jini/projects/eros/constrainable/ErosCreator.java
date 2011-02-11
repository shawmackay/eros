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
 * eros.jini.org : org.jini.projects.eros.constrainable
 * 
 * 
 * ErosCreator.java
 * Created on 05-Apr-2004
 * 
 * ErosCreator
 *
 */
package org.jini.projects.eros.constrainable;

import java.rmi.Remote;

import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;

import org.jini.glyph.chalice.builder.ProxyCreator;
import org.jini.projects.eros.ErosService;
import org.jini.projects.eros.ErosServiceMonitor;
import org.jini.projects.eros.ErosInterface;
import org.jini.projects.eros.admin.ErosAdmin;



/**
 * @author calum
 */
public class ErosCreator implements ProxyCreator { 
	/* @see utilities20.export.builder.ProxyCreator#create(java.rmi.Remote, net.jini.id.Uuid)
	 */
	public Remote create(Remote in, Uuid ID) {
		if(in instanceof ErosAdmin){
            
			System.out.println("Exporting ErosAdmin");
            return ErosAdminProxy.create((ErosAdmin) in , ID);
        }
        if( in instanceof ErosService){
            System.out.println("Exporting ErosService");
            return ErosProxy.create((ErosInterface) in, ID);
        }
        if(in instanceof RemoteEventListener){
            System.out.println("Exporting ErosServiceMonitor Listener");            
            return LookupNotifierProxy.create((RemoteEventListener) in, ID);
        }
		return null;
	}
}
