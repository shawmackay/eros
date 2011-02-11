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
 * DefaultErrorRecord.java
 *
 * Created on 13 February 2006, 15:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.eros.ui.erroranalyser.model;

import java.util.Date;

import org.jini.projects.eros.InstanceDetail;

/**
 *
 * @author calum.mackay
 */
public class DefaultErrorRecord implements ErrorRecord{
    
    private String message;
    private Date date;
    private String level;
    private String stacktrace;
    private long instancekey;
    private String arguments;
    private String methodName;
    private String className;
    private String exceptionName;
    private int linenumber;
    private transient InstanceDetail instanceDetail;
    
    /** Creates a new instance of DefaultErrorRecord */
    public DefaultErrorRecord( long instancekey,String message, Date date, String level, String stackTrace, String arguments,  String methodName,
            String className, String exceptionName, int linenumber) {
        this.message = message;
        
        
        this.date = date;
        this.level = level;
        this.stacktrace = stackTrace;
        this.arguments = arguments;
        this.instancekey = instancekey;
        
        this.className = className;
        this.methodName = methodName;
        this.exceptionName = exceptionName;
                
    }

    public long getInstanceKey(){
            return instancekey;
    }
    
    public void setInstanceDetail(InstanceDetail instanceDetail){
            this.instanceDetail = instanceDetail;
    }
    
    public InstanceDetail getInstanceDetail(){
            return instanceDetail;
    }
    
    public String getErrorMessage() {
        return message;
    }

    public String getIPAddress() {
        return instanceDetail.getIpAddress();
    }

    public String getApplicationName() {
        return instanceDetail.getApplicationName();
    }

    public Date getTime() {
        return date;
    }
    
      public String getLevel(){
          return level;
      }
    public String getStackTrace(){
        return stacktrace;
    }
    public String getArguments(){
        return arguments;
    }
    public String getGroup(){
        return instanceDetail.getErosGroups();
    }
    public String getMethodName(){
        return methodName;
    }
    public String getClassName(){
        return className;        
    }
    public String getExceptionName(){
        return exceptionName;
    }
   
    public String toString(){
        return "Instance " + instancekey + ": " + className + "#" + methodName + " @ " + date + " [" + level + "]";
                
    }

    public int getLineNumber() {
        // TODO Auto-generated method stub
        return linenumber;
    }
    
}
