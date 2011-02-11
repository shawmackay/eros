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
 * CentralStoreUnavailable.java
 *
 * Created on 24 May 2002, 14:04
 */

package org.blarty.eros.exception;

/**
 * Exception signifies the central store which errors are logged to, is
 * currently unavailable.
 * @author  Chrisl
 */
public class StoreUnavailableException extends Exception {

    /**
     * Creates a new instance of <code>CentralStoreUnavailable</code>
     * without detail message.
     */
    public StoreUnavailableException() {
    }

    /**
     * Constructs an instance of <code>CentralStoreUnavailable</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public StoreUnavailableException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>CentralStoreUnavailable</code>
     * with the specified detail message and the causing Exception.
     * @param msg the detail message.
     * @param exception the causing exception.
     */
    public StoreUnavailableException(String msg, Throwable exception) {
        super(msg, exception);
    }
    
}


