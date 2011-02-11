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
 * ErrorRecord.java
 *
 * Created on 13 February 2006, 15:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blarty.eros.ui.erroranalyser.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author calum.mackay
 */
public interface ErrorRecord extends Serializable{
    public String getErrorMessage();
    public String getIPAddress();
    public String getApplicationName();
    public Date getTime();
    public String getLevel();
    public String getStackTrace();
    public String getArguments();
    public String getGroup();
    public String getMethodName();
    public String getClassName();
    public String getExceptionName();
    public int getLineNumber();
    
}
