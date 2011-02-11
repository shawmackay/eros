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
package org.jini.projects.eros;

import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Holder class for the error detail to be sent from the client to the
 * server.
 * <p>All of the getter methods ensure that a null value is never returned.
 */
public class LoggingDetail implements Serializable, LogDetail {

    static final long serialVersionUID = 8545155620635713886L;

    private long instanceIdentifier;
    private Date date = null;
    private String message = null;
    private String stackTrace = null;
    private String argsString = null;
    private Integer code = null;
    private LogLevel level = null;
    private String methodName = null;
    private String className = null;
    private String exceptionName = null;

    /**
     * Creates the object and attempts to set a number of data fields relating 
     * to the machine the object is created on.
     */
    public LoggingDetail() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }


    public void setArguments(Object[] args) {
        this.argsString = null;
        StringBuffer sb = new StringBuffer();
        if( args != null ) {
            for (int i=0; i < args.length; i++) {
                sb.append(args[i].toString());
                if(i!=args.length-1)
                        sb.append(", ");
            }
            this.argsString = sb.toString();
        }
    }


    public void setCode(Integer code) {
        this.code = code;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public Date getDate() {
        return this.date != null ? this.date : new Date();
    }

    public String getMessage() {
        return this.message != null ? this.message : "Unknown";
    }

    public String getStackTrace() {
        return this.stackTrace != null ? this.stackTrace : "Unknown";
    }


    public String getArguments() {
        return this.argsString != null ? this.argsString : "None";
    }


    public Integer getCode() {
        return this.code != null ? this.code : new Integer(0);
    }


    public LogLevel getLevel() {
        if (this.level != null)
            return level;
        else
            return LogLevel.INFO;
    }
    
    
    

    /**
     * Overridden to provide a readable output of the data held within the object.
     * Similar to the <code>properties.toString()</code> output format.
     * @return A string representation of the data held within the object.
     */
    public String toString() {
        return  "Date: " + this.getDate() + "\n" +
                "Code: " + this.getCode().toString() + "\n" +
                "Message: " + this.getMessage() + "\n" +
                "StackTrace: " + this.getStackTrace() + "\n" +
                "Arguments: " + argsString + "\n" +
                "Level: " + this.getLevel();
    }

    /**
     * Provides equality matching for the object.
     * The test is performed against the equality of the following attributes:<br>
     * ApplicationName<br>
     * Level<br>
     * Time<br>
     * Message<br>
     */
    public boolean equals(Object obj) {
        if( obj instanceof org.jini.projects.eros.LoggingDetail) {
            LoggingDetail logDetail = (LoggingDetail) obj;
            if( logDetail.getInstanceIdentifier()== this.getInstanceIdentifier() &&
                    logDetail.getLevel() == this.getLevel() &&
                        logDetail.getDate().getTime() == this.getDate().getTime() &&
                            logDetail.getMessage().equals(this.getMessage()) ) {
                                
                return true;
            }
        }
        return false;
    }

public String getClassName() {
        return className;
}

public void setClassName(String className) {
        this.className = className;
}

public String getMethodName() {
        return methodName;
}

public void setMethodName(String methodName) {
        this.methodName = methodName;
}

public String getExceptionName() {
        return exceptionName;
}

public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
}

public void setInstanceIdentifier(long id) {
        // TODO Auto-generated method stub
        this.instanceIdentifier = id;
}

public long getInstanceIdentifier() {
        // TODO Auto-generated method stub
        return this.instanceIdentifier;
}

}
