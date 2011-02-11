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

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Date;
import java.net.InetAddress;

/**
 * Data holder class that provides all the statistical information for an
 * instance of an Eros service.
 */
public class ErosServiceStats implements Serializable {
    static final long serialVersionUID = 105753444991903314L;
    private long totalLogs = 0;
    private long totalLoggersSupplied = 0;
    private Hashtable logsPerLevel = new Hashtable();
    private Hashtable logsPerApp = new Hashtable();
    private Hashtable logsPerOS = new Hashtable();
    private Hashtable logsPerUser = new Hashtable();
    private long lastLog = 0;
    private String centralStore = "";
    private long upTime = 0;
    private String internalLog = "";
    private String hostName = "Unknown";
    private String hostAddress = "Unknown";
    private long errorQueSize = 0;
    private long totalMemory = 0;
    private long freeMemory = 0;
    
    /**
     * Trys to obtain the machine information for which it is running on.     
     * Any exceptions encountered are ignored as this information is 
     * not critical
     */
    public ErosServiceStats() {
        upTime = new Date().getTime();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            hostAddress = localHost.getHostAddress();
            hostName = localHost.getHostName();
        } catch (Exception exc) {
            //Ignore all exceptions as not crtical
        }
    }

    /**
     * Updates the internal totals with details from the logging details supplied.
     * @param logDetail The details that should be applied to the internal counts
     */
    public synchronized void update(InstanceDetail instanceDetail,LogDetail logDetail) {
        //Increment the total number of logs.
        this.totalLogs++;

        LogLevel level = logDetail.getLevel();

        //Increment the count of logs for this level.
        Long count = (Long) logsPerLevel.get(level);
        if( count != null ) {
            logsPerLevel.put(level, new Long((count.longValue() + 1)));
        } else {
            logsPerLevel.put(level, new Long(1));
        }

        //Increment the count for application based on logging level.
        Hashtable logsPerLevelPerApp = (Hashtable) logsPerApp.get(instanceDetail.getApplicationName());
        if( logsPerLevelPerApp != null ) {
            count = (Long) logsPerLevelPerApp.get(level);
            if( count != null ) {
                logsPerLevelPerApp.put(level, new Long((count.longValue() + 1)));
            } else {
                logsPerLevelPerApp.put(level, new Long(1));
            }
        } else {
            logsPerLevelPerApp = new Hashtable();
            logsPerLevelPerApp.put(level, new Long(1));
            logsPerApp.put( instanceDetail.getApplicationName(), logsPerLevelPerApp );
        }

        //Increment the count of logs for this OS
        count = (Long) logsPerOS.get(instanceDetail.getOsName());
        if( count != null ) {
            logsPerOS.put( instanceDetail.getOsName(), new Long((count.longValue() + 1)) );
        } else {
            logsPerOS.put( instanceDetail.getOsName(), new Long(1) );
        }
        
        //Increment the count of logs for this user
        count = (Long) logsPerUser.get(instanceDetail.getOsUser());
        if( count != null ) {
            logsPerUser.put( instanceDetail.getOsUser(), new Long((count.longValue() + 1)) );
        } else {
            logsPerUser.put( instanceDetail.getOsUser(), new Long(1) );
        }

        //Update the time of the last log to now.
        this.lastLog = new java.util.Date().getTime();

    }

    /**
     * Sets the data for the central store
     * @param info A string representation of the central store info.
     */
    public void setCentralStoreInfo(String info) {
        this.centralStore = info;
    }

    /**
     * Updates the internal count for the number of loggers supplied to clients.
     */
    public void updateLoggersSupplied() {
        this.totalLoggersSupplied++;
    }

    /**
     * Sets the string representation of the current logging status of the server
     * @param log The internal logging level of the server
     */
    public void setInternalLogging(String log) {
        this.internalLog = log;
    }

    /**
     * Sets the size of the error queue currently maintained at the server.
     * @param size The size of the queue.
     */
    public void setErrorQueSize(long size) {
        this.errorQueSize = size;
    }
    
    /**
     * Sets the total memory that is available to the server.
     * @param total The total size of the available memory.
     */
    public void setTotalMemory(long total) {
        this.totalMemory = total;
    }
    
    /**
     * Sets the free memory that is available to the server.
     * @param free The total size of the unused memory.
     */
    public void setFreeMemory(long free) {
        this.freeMemory = free;
    }
    
    /**
     * Provides the total no of logs that have been processed by the server.
     * @return The number of logs
     */
    public long getTotalLogs() {
        return this.totalLogs;
    }

    /**
     * Provides the total number of loggers supplied to clients
     * @return The number of loggers supplied.
     */
    public long getTotalLoggersSupplied() {
        return this.totalLoggersSupplied;
    }

    /**
     * Provides the time of the last log processed by the server.
     * @return The last log time.
     */
    public long getLastLogTime() {
        return this.lastLog;        
    }

    /**
     * Provides the central store information for the server
     * @return The string representation of the central store info
     */
    public String getCentralStoreInfo() {
        return this.centralStore;
    }

    /**
     * Provides the number of logs that have been processed for each level.
     * 
     * @return The Hashtable containing the counts for the levels.
     * The key of the table is an <code>Integer</code> object that represents 
     * the level of the log.
     * The value is a <code>Long</code> object that is the count of logs for 
     * that level.
     */
    public Hashtable getLogsPerLevel() {
        return this.logsPerLevel;
    }

    /**
     * Provides the number of logs per level for each application.
     * @return A Hashtable containing the counts for the applications.
     * The key of the table is a <code>String</code> object that is the name
     * of the application that has made the logs.
     * The value is a <code>Hashtable</code> object that contains the logging 
     * levels in the same manner as the <code>Hashtable</code> returned from 
     * the <code>getLogsPerLevel()</code> method.
     * @see #getLogsPerLevel()
     */
    public Hashtable getLogsPerApp() {
        return this.logsPerApp;
    }

    /**
     * Provides the number of logs that have been receieved for each particular
     * operating system.
     * @return A Hashtable containing the count for each operating system.
     * The key is a <code>String</code> object which contains the operating 
     * system name.
     * The value is a <code>Long</code> object which is the total number of 
     * logs for that OS.
     */
    public Hashtable getLogsPerOS() {
        return this.logsPerOS;
    }
    
    /**
     * Provides the number of logs that have been recieved for each particular
     * user.
     * @return A Hashtable containing the count for each user.
     * The key is a <code>String</code> object which contains the user name 
     * The value is a <code>Long</code> object which is the total number of 
     * logs for that user.
     */
    public Hashtable getLogsPerUser() {
        return this.logsPerUser;
    }

    /**
     * Provides the length of time that the server has been available.
     * @return The length of time 
     */
    public long getUpTime() {
        return (new java.util.Date().getTime() - this.upTime);
    }

    /**
     * Provides the current level for the internal loging of the server.
     * @return The level currently used by the server for it's internal logging
     */
    public String getInternalLog() {
        return this.internalLog;
    }

    /**
     * Provides the name of the Host upon which the server is running on.
     * @return The name of the hsot machine.
     */
    public String getHostName() {
        return this.hostName;
    }

    /**
     * Provides the IP address for the machine the server is running on.
     * @return The IP address.
     */
    public String getHostAddress() {
        return this.hostAddress;
    }

    /**
     * Provides the size of the error queue currently maintained at the server.
     * @return The current queue size.
     */
    public long getErrorQueSize() {
        return this.errorQueSize;
    }
    
    /**
     * Provides the total memory available to the server.
     * @return The total memory available.
     */
    public long getTotalMemory() {
        return this.totalMemory;
    }
    
    /**
     * Provides the free memory available to the server.
     * @return The free memory available.
     */
    public long getFreeMemory() {
        return this.freeMemory;
    }

    public static void main(String[] args) {
//        ErosServiceStats ess = new ErosServiceStats();
//        LoggingDetail ld = new LoggingDetail();
//        ld.setLevel(LogLevel.INFO);
//        ess.update(ld);
//        ld = new LoggingDetail();
//        ld.setLevel(LogLevel.WARN);
//        ess.update(ld);
//        ld = new LoggingDetail();
//        ld.setLevel(LogLevel.INFO);
//        ess.update(ld);
//
//        System.out.println("No of info - " + ess.logsPerLevel.get(LogLevel.INFO));
//        System.out.println("No of warn - " + ess.logsPerLevel.get(LogLevel.WARN));
    }
}
