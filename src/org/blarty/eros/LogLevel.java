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
 * LogLevel.java
 *
 * Created on 06 February 2002, 16:58
 * @author  Chrisl
 */

package org.blarty.eros;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Defines the various levels of logging that are provided as
 * part of the Eros service.
 */
public class LogLevel implements Serializable {
    static final long serialVersionUID = -5907289486447563327L;

    public static final int FATAL_VALUE = 4;
    public static final int ERROR_VALUE = 3;
    public static final int WARN_VALUE = 2;
    public static final int INFO_VALUE = 1;
    public static final int DEBUG_VALUE = 0;

    private static final String FATAL_STRING = "Fatal";
    private static final String ERROR_STRING = "Error";
    private static final String WARN_STRING = "Warning";
    private static final String INFO_STRING = "Info";
    private static final String DEBUG_STRING = "Debug";

    /**
     * Represents a fatal or serious log.
     */
    public static final LogLevel FATAL = new LogLevel(FATAL_VALUE);
    /**
     * Represents an error log.
     */
    public static final LogLevel ERROR = new LogLevel(ERROR_VALUE);
    /**
     * Represents a warning log.
     */
    public static final LogLevel WARN = new LogLevel(WARN_VALUE);
    /**
     * Represents an information log.
     */
    public static final LogLevel INFO = new LogLevel(INFO_VALUE);
    /**
     * Represents a debugging log.
     */
    public static final LogLevel DEBUG = new LogLevel(DEBUG_VALUE);

    private int level;
    
    /**
     * Creates a new instance of LogLevel.
     */
    private LogLevel(int level) {
        this.level = level;
    }

    /**
     * Provides a numeric representation of this level.
     * @return A numerical representation of this level.
     */
    public int intValue() {
        return level;
    }

    /**
     * Provides a textual representation of this level.
     * @return A textual representation of this level.
     */
    public String toString() {
        switch (level) {
            case FATAL_VALUE :
                return FATAL_STRING;
            case ERROR_VALUE :
                return ERROR_STRING;
            case WARN_VALUE :
                return WARN_STRING;
            case INFO_VALUE :
                return INFO_STRING;
            case DEBUG_VALUE :
                return DEBUG_STRING;
            default :
                return "";
        }
    }

    /**
     * Checks for equality based on the int value of the objects, obtained
     * from the {@link #intValue() intValue()} method.
     * @param obj The object to test for equality.
     * @return true if the return of intValue() from both objects are equal.
     */
    public boolean equals(Object obj) {
        if( obj instanceof LogLevel ) {
            return ((LogLevel)obj).intValue() == this.intValue();
        }
        return false;
    }

    /**
     * Provides the hashCode as the same as the return from {@link #intValue() intValue()}.
     * @return the value supplied from the {@link #intValue() intValue()} method.
     */
    public int hashCode() {
        return this.intValue();
    }

}
