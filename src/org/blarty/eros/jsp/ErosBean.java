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
 * Date: 25-Jul-02
 * Time: 13:20:22
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.blarty.eros.jsp;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class that provides the details for the JSP that displays error detail.
 */
public class ErosBean {

    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int PREVIOUSDAY = 2;
    public static final int OLD = 3;

    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private static String DB_URL = "jdbc:oracle:thin:@nts4_006.countrywide-assured.co.uk:1521:SSDB";
    private static String REGION = "production";
    private static String RESTRICTED_SQL =
                "SELECT TIME_STAMP, APPLICATION, ARGUMENTS, EXCEPTION_TYPE, " +
                "STACK_TRACE FROM MIER " +
		        "WHERE TRUNC(TIME_STAMP) > TRUNC(SYSDATE-3) " +
		        "AND APPLICATION <> 'Athena' " +
		        "AND TO_CHAR(TIME_STAMP,'HH') <> '05' " +
                "AND REGION LIKE '" + REGION + "%' " +
                "ORDER BY TIME_STAMP DESC";
    private static String ALL_SQL =
                "SELECT TIME_STAMP, APPLICATION, ARGUMENTS, EXCEPTION_TYPE, " +
                "STACK_TRACE FROM MIER " +
		        "WHERE TRUNC(TIME_STAMP) > TRUNC(SYSDATE-3) " +
                "AND REGION LIKE '" + "%' " +
                "ORDER BY TIME_STAMP DESC";

    private Timestamp today = null;
    private Timestamp yesterday = null;
    private Timestamp previousDay = null;

    private Connection conn = null;
    private ResultSet resultSet = null;
    private Timestamp timeStamp = null;
    private String application = null;
    private String arguments = null;
    private String exception = null;
    private String stackTrace = null;
    private int day;

    /**
     * Creates the bean and sets the today, yesterday and previousDay values.
     * @throws ClassNotFoundException If the Oracle driver cannot be loaded.
     */
    public ErosBean() throws ClassNotFoundException  {
        try {
            Class.forName(ORACLE_DRIVER);
            Calendar calendar = new GregorianCalendar();
            java.util.Date tdy = new java.util.Date();

            calendar.setTime(tdy);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            today = new Timestamp(calendar.getTime().getTime());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            yesterday = new Timestamp(calendar.getTime().getTime());
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            previousDay = new Timestamp(calendar.getTime().getTime());

            System.out.println("today = " + today.toString());
            System.out.println("yesterday = " + yesterday.toString());
            System.out.println("previousDay = " + previousDay.toString());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Sets the URL for the database to retrieve the error information from.
     * @param url The database URL in Oracle thin driver format.
     */
    public void setDBUrl(String url) {
        DB_URL = url;
    }

    /**
     * Connects to the database and retrieves the required data.
     * @param showAll If this is true, then all error information will be
     *                  retrieved, if false, then a restricted set of information
     *                  will be retrieved.
     * @throws SQLException Any problem retrieving the data.
     */
    public void refreshData(boolean showAll) throws SQLException {
        conn = DriverManager.getConnection(DB_URL, "auto_at", "auto_at");
        String sql = "";
        if(showAll)
            sql = ALL_SQL;
        else
            sql = RESTRICTED_SQL;
        PreparedStatement ps = conn.prepareStatement(sql);
        resultSet = ps.executeQuery();
    }

    /**
     * Checks if there are further records available to be processed.
     * Calling this method advances the pointer to the next record if it is
     * available.
     * @return true if the next record has been obtained, or false if there
     *              are no further records available.
     * @throws SQLException Any problems moving to the next available record.
     */
    public boolean hasNext() throws SQLException {
        if( resultSet != null )
            if( resultSet.next() ) {
                this.timeStamp = resultSet.getTimestamp(1);
                this.application = resultSet.getString(2);
                this.arguments = resultSet.getString(3);
                this.exception = resultSet.getString(4);
                this.stackTrace = resultSet.getString(5);

                if( timeStamp.getTime() > today.getTime() ) {
                    day = TODAY;
                } else if( timeStamp.getTime() > yesterday.getTime() ) {
                    day = YESTERDAY;
                } else if( timeStamp.getTime() > previousDay.getTime() ) {
                    day = PREVIOUSDAY;
                } else {
                    day = OLD;
                }

                return true;
            } else {
                return false;
            }
        else
            return false;
    }

    /**
     * Closes any open results and connections to the database.
     * @throws SQLException Any problems closing the connections.
     */
    public void close() throws SQLException {
        resultSet.close();
        conn.close();
    }

    /**
     * Provides the day indicator for the current record.
     * @return The indicator of the daya of the current record as defined
     *              in this class.
     */
    public int getDay() {
        return day;
    }

    /**
     * Provides the formatted date and time for the current record.
     * @return The date and time formatted as '31-12-2002 23:59:59'
     */
    public String getTimeStamp() {
        java.util.Date d = new java.util.Date();
        d.setTime(timeStamp.getTime());
        return sdf.format(d);
    }

    /**
     * Provides the name of the application for the current record.
     * @return The name of the application for this record.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Provides the arguments for the current error record.
     * @return The arguments for the current record.
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * Provides the exception details for the current record.
     * @return The exception detail for the current record.
     */
    public String getException() {
        return exception;
    }

    /**
     * Provides the stacktrace detail for the current record.
     * @return The stacktrace details for the record.
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Supplies the region that is being used to retrieve the error details.
     * @return The region that the error details are being displayed for.
     */
    public static String getRegion() {
        return REGION;
    }

    /**
     * Sets the region that will be used to retrieve the error details.
     * @param region The region to retrieve error details for.
     */
    public static void setRegion(String region) {
        ErosBean.REGION = region;
    }

    /**
     * Testing method.
     * @param args None.
     */
    public static void main(String[] args) {
        try {
            ErosBean eb = new ErosBean();
            eb.refreshData(true);
            while(eb.hasNext()) {
                System.out.println(eb.getDay());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
        }
    }
}
