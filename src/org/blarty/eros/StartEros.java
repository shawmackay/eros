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


import java.io.IOException;
import java.rmi.activation.ActivationException;
import java.rmi.Remote;

/**
 * Starter class for the Eros service.
 * <p>
 * Utilises the starter utility supplied by Sun 
 * <code>com.sun.jini.start.ServiceStarter</code>.
 */
public class StartEros {
    
    public StartEros() {                
    }

    /**
     * Required method for use with
     * <code>com.sun.jini.start.ServiceStarter</code>.
     * @param serverStub
     * @return Supplies an instantiated service.
     */
    public static ErosService create(Remote serverStub) {
	    return (ErosService)serverStub;
    }

    /**
     * Used to create an activatable instance of the ErosService.
     * The arguments required are documented in the super class.
     */    
    public static void main(String[] args) {
//        ServiceStarter.create(args,
//			      StartEros.class.getName(),
//			      ErosServiceImpl.class.getName(),
//			      "service");
    }
        
}
