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

package org.blarty.eros.logging;

import java.util.logging.*;
import java.sql.*;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Class that provides JDBC logging using the java.util.logging.Handler
 * interface.  Takes the supplied LogRecord and inserts the details into
 * a database table.
 */
public class JDBCHandler extends Handler {

    private final static String insertSQL=
            "INSERT INTO ERST (TIME_STAMP, APPLICATION, MESSAGE, ERROR_LEVEL, " +
            "STACKTRACE, ARGUMENTS, GROUPS, HOST) " +
            "VALUES(?,?,?,?,?,?,?,?)";

    private Connection connection;
    private PreparedStatement prepInsert;
    private String[] region = new String[]{"Unknown"};

    /**
    * Creates the handler and connects to the database.
    * @param driverString The JDBC driver to use.
    * @param connectionString The connection string that
    *        specifies the database to use.
    */
    public JDBCHandler(String driverString, String connectionString,
                        String user, String password) {
        try {
            Class.forName(driverString);
            System.out.println("JDBCHandler creating connection \n\t"+connectionString+"\n\t"+user+"\n\t"+password);
            connection = DriverManager.getConnection(connectionString, user, password);
            prepInsert = connection.prepareStatement(insertSQL);
        } catch ( ClassNotFoundException e ) {
            this.getErrorManager().error("Error in initialisation.", e, 0);
            e.printStackTrace();
        } catch ( SQLException e ) {
            this.getErrorManager().error("Error in initialisation.", e, 0);
            e.printStackTrace();
        }
    }

    /**
     * Sets the region that is used in the log.
     * @param region The region this handler is working for.
     */
    public void setRegion(String[] region) {
        this.region = region;
    }

    /**
    * Internal method used to truncate a string to a specified width.
    * Used to ensure that SQL table widths are not exceeded.
    * @param str The string to be truncated.
    * @param length The maximum length of the string.
    * @return The string truncated.
    */
    private String truncate(String str,int length) {
        if ( str.length()<length )
            return str;
        return( str.substring(0,length) );
    }

    private static String arrayToString(Object[] objs) {
        if(objs != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < objs.length; i++) {
                Object obj = objs[i];
                sb.append(obj.toString());
                sb.append(",");
            }
            return sb.toString().substring(0, sb.length()-1);
        } else {
            return "";
        }
    }

    /**
     * Overidden to stop the level being changed.  This call is ignored
     * as only logs over Level.WARNING will be logged.
     * @param newLevel
     * @throws SecurityException
     */
    public synchronized void setLevel(Level newLevel) throws SecurityException {
    }

    public boolean isLoggable(LogRecord record) {
        return (record.getLevel().intValue() >= this.getLevel().intValue());
    }

    /**
     * Overidden to provide the static level of the logger.
     * @return The static level - Level.WARNING
     */
    public synchronized Level getLevel() {
        return Level.WARNING;
    }

    /**
    * Overridden method used to capture log entries and put them
    * into a JDBC database.
    * @param record The log record to be stored.
    */
    public void publish(LogRecord record) {
        if(this.isLoggable(record)) {
            System.out.println("JDBCHandler is loggable");
            if ( getFilter()!=null ) {
                if ( !getFilter().isLoggable(record) )
                    return;
            }
            System.out.println("JDBCHandler about to publish");
            try {
                prepInsert.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                prepInsert.setString(2, "ErosService");
                prepInsert.setString(3, record.getMessage());
                prepInsert.setString(4, ErosHandlerImpl.getErosLogLevel(record.getLevel()).toString());
                Throwable ex = record.getThrown();
                String stackTrace = "";
                if(ex != null) {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        stackTrace = sw.toString();
                    } catch (Exception exc) {
                        //Ignore
                    }
                }
                prepInsert.setString(5, truncate(stackTrace, 1000));
                prepInsert.setString(6, truncate(arrayToString(record.getParameters()), 300));
                prepInsert.setString(7, truncate(arrayToString(region), 50));
                StringBuffer host = new StringBuffer();
                try {
                    InetAddress localHost = InetAddress.getLocalHost();
                    host.append(localHost.getHostAddress() + "$");
                    host.append(localHost.getHostName() + "$");
                    host.append(System.getProperty("os.name") + "; ");
                    host.append(System.getProperty("os.arch") + "; ");
                    host.append(System.getProperty("os.version") + "$");
                    host.append(System.getProperty("java.version") + "$");
                    host.append(System.getProperty("user.name"));
                } catch (Exception exc) {}
                prepInsert.setString(8, truncate(host.toString(), 100));

                prepInsert.executeUpdate();
                connection.commit();

            } catch ( SQLException e ) {
                this.getErrorManager().error("Exception inserting log.", e, 1);
                e.printStackTrace();
            }
        }
    }

    /**
    * Closes the database connection
    */
    public void close() {
        try {
            if ( connection!=null )
                connection.close();
        } catch ( SQLException e ) {
            this.getErrorManager().error("Error closing connection.", e, 2);
        }
    }

    /**
     * No action required.
     */
    public void flush() {
    }

    public static void main(String[] args) {
        JDBCHandler jdbcHandler = new JDBCHandler("oracle.jdbc.driver.OracleDriver",
                "jdbc:oracle:thin:@nts4_005.countrywide-assured.co.uk:1521:SSDT",
                "chrisl", "chrisl");
        Logger l = Logger.getLogger("JDBCLogger");
        l.addHandler(jdbcHandler);
        l.log(Level.WARNING, "This is a test for JDBCHandler");
    }

}

