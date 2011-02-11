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
 * Created by IntelliJ IDEA.
 * User: Chrisl
 * Date: 21-Aug-2002
 * Time: 14:27:05
 */
package org.blarty.eros;

import java.util.Date;

/**
 * Interface that defines what logging information should be generated at the
 * client and supplied to the server.
 */
public interface LogDetail {

        /**
         * Sets the identifier for the applications instance information
         * 
         * @param id
         *                the identifier to use
         */
        void setInstanceIdentifier(long id);

        long getInstanceIdentifier();
        
        /**
         * Sets the date that is associated with this error. Should be that date
         * and time that the error occured, not the date the object is created.
         * 
         * @param date
         *                errDate The date of the error.
         */
        void setDate(Date date);

        /**
         * Sets the message that is associated with this error. Should be a
         * short summary of the error.
         * 
         * @param message
         *                The short message.
         */
        void setMessage(String message);

        /**
         * Sets the stack trace associated with this error.
         * 
         * @param stackTrace
         *                A string representing the stack trace for the error.
         */
        void setStackTrace(String stackTrace);

        /**
         * Sets the application name that this error was generated in.
         * 
         * @param appName
         *                The applciation name.
         */
        // void setApplicationName(String appName);
        /**
         * Sets the arguments that are associated with this error. These should
         * by key identifiers useful for investigating the error.
         * 
         * @param args
         *                The object arguments associated with this error.
         */
        void setArguments(Object[] args);

        /**
         * Sets the Jini groups that are related to this error.
         * 
         * @param groups
         *                The Jini groups that relate to this error.
         */
        // void setGroups(String[] groups);
        /**
         * Sets the error code that relates to this error.
         * 
         * @param code
         *                A value that relates to the error.
         */
        void setCode(Integer code);

        /**
         * Sets the level of the log
         * 
         * @param level
         *                The level of the log.
         */
        void setLevel(LogLevel level);

        /**
         * Sets a String representing the class name of where the exception was
         * thrown from. This is specified in the form of packageName.className
         * 
         * @param className
         *                the fully qualified class name;
         */
        void setClassName(String className);

        /**
         * Sets a String representing the method name of where the exception was
         * thrown from This is specified in the format of className#methodName
         * (no package)
         * 
         * @param method
         *                the method name;
         */
        void setMethodName(String method);

        /**
         * Sets a String representing the class name of the thrown exception.
         * This is specified in the format of package.className
         * 
         * @param method
         *                the method name;
         */
        void setExceptionName(String method);

        /**
         * @return The date that has been set for this error. If no date has
         *         been set the current date will be returned.
         */
        Date getDate();

        /**
         * @return The message that was set for this error. If no message was
         *         set then 'Unknown' will be returned.
         */
        String getMessage();

        /**
         * @return The stack trace that was set for this error. If no stack
         *         trace was set then 'Unknown' will be returned.
         */
        String getStackTrace();

        /**
         * @return The application name that was set for this error. If no
         *         application name was set then 'Unknown' will be returned.
         */
        // String getApplicationName();
        /**
         * @return The arguments that were set for this error. If no arguments
         *         were set then 'None' will be returned.
         */
        String getArguments();

        /**
         * Returns the groups that have been associated with this error.
         * 
         * @return The groups set. If no groups have been set then 'Unknown'
         *         will be returned.
         */
        // String getGroups();
        /**
         * Returns the error code that has been set for this error.
         * 
         * @return The error code set for this error. If this has not been set
         *         then an Integer with the value of zero will be returned.
         */
        Integer getCode();

        /**
         * Returns the host name for the machine the log was generated on
         * 
         * @return The host name. If this has not been set then 'Unknown' will
         *         be returned.
         */
        // String getHostName();
        /**
         * Returns the host address for the machine the log was generated on
         * 
         * @return The host address. If this has not been set then 'Unknown'
         *         will be returned.
         */
        // String getHostAddress();
        /**
         * Returns the level set for this log.
         * 
         * @return The level for this log.
         */
        LogLevel getLevel();

        /**
         * Returns a String representing the details of the operating system the
         * log was generated on.
         * 
         * @return The operating system details. If this has not been set then
         *         'Unavailable' will be returned.
         */
        // String getOSDetails();
        /**
         * Returns a String representing the user name who generated this log.
         * 
         * @return The user name. If this has not been set then 'Unavailable'
         *         will be returned.
         */
        // String getUserName();
        /**
         * Returns a String representing the 'java.version' system property from
         * the machine the log was generated on.
         * 
         * @return The java version. If this has not been set then 'Unavailable'
         *         will be returned.
         */
        // String getJavaVersion();
        /**
         * Returns a String representing the class name of where the exception
         * was thrown from. This is specified in the form of
         * packageName.className
         * 
         * @return the fully qualified class name;
         */
        String getClassName();

        /**
         * Returns a String representing the method name of where the exception
         * was thrown from This is specified in the format of
         * className#methodName (no package)
         * 
         * @return the method name;
         */
        String getMethodName();

        /**
         * Returns a String representing the classname of the thrown
         * exception.If a warning log was called without an exception, this will
         * be RuntimeInfo This is specified in the format of
         * className#methodName (no package)
         * 
         * @return the classname of the exception;
         */
        String getExceptionName();
}
