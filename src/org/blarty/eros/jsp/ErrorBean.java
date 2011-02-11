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
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Class that provides the details for the JSP that displays error detail.
 */
public class ErrorBean {

    public static final int TODAY = 0;

    public static final int YESTERDAY = 1;

    public static final int PREVIOUSDAY = 2;

    public static final int OLD = 3;

    public static final int TIME_COL = 0;

    public static final int APPLICATION_COL = 1;

    public static final int MESSAGE_COL = 2;

    public static final int LEVEL_COL = 3;

    private static final String ORACLE_DRIVER = ErrorResBundle.getString("ErrorBean.JDBCDriver"); //$NON-NLS-1$

    private static final SimpleDateFormat sdf = new SimpleDateFormat(ErrorResBundle.getString("ErrorBean.DateFormat")); //$NON-NLS-1$

    private String DB_URL = ErrorResBundle.getString("ErrorBean.JDBCURL"); //$NON-NLS-1$

    private String REGION = ErrorResBundle.getString("ErrorBean.Region"); //$NON-NLS-1$

    private String RESTRICTED_SQL = // Needs two
                                                                // single quotes
                                                                // as used in
                                                                // MessageFormat
    ErrorResBundle.getString("ErrorBean.RestrictedSQL"); //$NON-NLS-1$

    private String ALL_SQL = ErrorResBundle.getString("ErrorBean.AllSQL"); //$NON-NLS-1$

    private String ID_SQL = ErrorResBundle.getString("ErrorBean.IDSQL"); //$NON-NLS-1$

    private Timestamp today = null;

    private Timestamp yesterday = null;

    private Timestamp previousDay = null;

    private Connection conn = null;

    private ResultSet resultSet = null;

    private int id = 0;

    private Timestamp timeStamp = null;

    private String application = null;

    private String message = null;

    private String level = null;

    private String stackTrace = null;

    private String arguments = null;

    private String groups = null;

    private String host = null;

    private int day;

    private int lastColSorted = TIME_COL;

    /**
     * Creates the bean and sets the today, yesterday and previousDay values.
     * 
     * @throws ClassNotFoundException
     *                    If the Oracle driver cannot be loaded.
     */
    public ErrorBean() throws ClassNotFoundException {
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

            System.out.println("today = " + today.toString()); //$NON-NLS-1$
            System.out.println("yesterday = " + yesterday.toString()); //$NON-NLS-1$
            System.out.println("previousDay = " + previousDay.toString()); //$NON-NLS-1$
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Sets the URL for the database to retrieve the error information from.
     * 
     * @param url
     *                   The database URL in Oracle thin driver format.
     */
    public void setDBUrl(String url) {
        DB_URL = url;
    }

    /**
     * Connects to the database and retrieves the required data.
     * 
     * @param showAll
     *                   If this is true, then all error information will be retrieved,
     *                   if false, then a restricted set of information will be
     *                   retrieved.
     * @throws SQLException
     *                    Any problem retrieving the data.
     */
    public void refreshData(boolean showAll, int sortCol, boolean ascending) throws SQLException {
        connect();
        String sql = ""; //$NON-NLS-1$
        if (showAll)
            sql = ALL_SQL;
        else
            sql = MessageFormat.format(RESTRICTED_SQL, new Object[] { REGION });

        String order = " "; //$NON-NLS-1$
        if (!ascending)
            order = " DESC"; //$NON-NLS-1$

        sql += " ORDER BY "; //$NON-NLS-1$
        switch (sortCol) {
        case TIME_COL:
            sql += "TIME_STAMP" + order; //$NON-NLS-1$
            break;
        case APPLICATION_COL:
            sql += "APPLICATION" + order + ",TIME_STAMP DESC"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        case LEVEL_COL:
            sql += "ERROR_LEVEL" + order + ",TIME_STAMP DESC"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        case MESSAGE_COL:
            sql += "MESSAGE" + order + ",TIME_STAMP DESC"; //$NON-NLS-1$ //$NON-NLS-2$
            break;
        default:
            sql += "TIME_STAMP" + order; //$NON-NLS-1$
            break;
        }

        PreparedStatement ps = conn.prepareStatement(sql);
        resultSet = ps.executeQuery();
    }

    private void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, ErrorResBundle.getString("ErrorBean.Username"), ErrorResBundle.getString("ErrorBean.Password")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Checks if there are further records available to be processed. Calling
     * this method advances the pointer to the next record if it is available.
     * 
     * @return true if the next record has been obtained, or false if there are
     *               no further records available.
     * @throws SQLException
     *                    Any problems moving to the next available record.
     */
    public boolean hasNext() throws SQLException {
        if (resultSet != null)
            if (resultSet.next()) {
                this.id = resultSet.getInt(1);
                this.timeStamp = resultSet.getTimestamp(2);
                this.application = resultSet.getString(3);
                this.message = resultSet.getString(4);
                this.level = resultSet.getString(5);
                this.stackTrace = resultSet.getString(6);
                this.arguments = resultSet.getString(7);
                this.groups = resultSet.getString(8);
                this.host = resultSet.getString(9);

                if (timeStamp.getTime() > today.getTime()) {
                    day = TODAY;
                } else if (timeStamp.getTime() > yesterday.getTime()) {
                    day = YESTERDAY;
                } else if (timeStamp.getTime() > previousDay.getTime()) {
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
     * 
     * @throws SQLException
     *                    Any problems closing the connections.
     */
    public void close() throws SQLException {
        resultSet.close();
        conn.close();
    }

    /**
     * Provides the day indicator for the current record.
     * 
     * @return The indicator of the daya of the current record as defined in
     *               this class.
     */
    public int getDay() {
        return day;
    }

    /**
     * Provides the formatted date and time for the current record.
     * 
     * @return The date and time formatted as '31-12-2002 23:59:59'
     */
    public String getTimeStamp() {
        java.util.Date d = new java.util.Date();
        d.setTime(timeStamp.getTime());
        return sdf.format(d);
    }

    /**
     * Provides the name of the application for the current record.
     * 
     * @return The name of the application for this record.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Provides the arguments for the current error record.
     * 
     * @return The arguments for the current record.
     */
    public String getArguments() {
        return arguments;
    }

    /**
     * Provides the stacktrace detail for the current record.
     * 
     * @return The stacktrace details for the record.
     */
    public String getStackTrace() {
        return stackTrace.replaceAll("\n", "</br>"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getLevel() {
        return level;
    }

    public String getGroups() {
        return groups;
    }

    public String getHost() {
        return host;
    }

    /**
     * Supplies the region that is being used to retrieve the error details.
     * 
     * @return The region that the error details are being displayed for.
     */
    public String getRegion() {
        return REGION;
    }

    /**
     * Sets the region that will be used to retrieve the error details.
     * 
     * @param region
     *                   The region to retrieve error details for.
     */
    public void setRegion(String region) {
        REGION = region;
    }

    public boolean getErrorDetail(int id) throws SQLException {
        boolean result = false;
        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(ID_SQL);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            result = this.hasNext();
        } finally {
            close();
        }
        return result;
    }

    /**
     * Testing method.
     * 
     * @param args
     *                   None.
     */
    public static void main(String[] args) {
        try {
            ErrorBean eb = new ErrorBean();
            eb.setDBUrl("jdbc:oracle:thin:@nts4_005.countrywide-assured.co.uk:1521:SSDT"); //$NON-NLS-1$
            eb.setRegion("chris"); //$NON-NLS-1$
            eb.refreshData(false, ErrorBean.TIME_COL, false);
            while (eb.hasNext()) {
                System.out.println(eb.getDay());
            }
            eb.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
        }
    }
}
