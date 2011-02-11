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
 * eros.jini.org : org.jini.projects.eros.server
 * 
 * 
 * GenericJDBCStore.java
 * Created on 25-Oct-2004
 * 
 * GenericJDBCStore
 *
 */

package org.jini.projects.eros.server;

import java.sql.Statement;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jini.projects.eros.InstanceDetail;
import org.jini.projects.eros.InstanceDetailImpl;
import org.jini.projects.eros.LogDetail;
import org.jini.projects.eros.exception.StoreUnavailableException;
import org.jini.projects.eros.logging.JDBCHandler;
import org.jini.projects.eros.ui.erroranalyser.model.DefaultErrorModel;
import org.jini.projects.eros.ui.erroranalyser.model.DefaultErrorRecord;
import org.jini.projects.eros.ui.erroranalyser.model.ErrorModel;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author calum
 */
public class GenericJDBCStore implements ErosBackendStore {

    private String pattern = "at [\\.A-Z\\$a-z0-9]*\\([\\.A-Z\\$a-z0-9:]*\\)";

    private Pattern regex = Pattern.compile(pattern);

    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    private long connInfoLastModified = 0;

    private File connectionInfo = null;

    private boolean definedColumns = false;

    private boolean connectionError = false;

    private Document document = null;

    private DocumentBuilder docBuild = null;

    private Map columnMap = null;

    private String url = "";

    private String user = "";

    private String pass = "";

    private String table = "";

    private Connection conn = null;

    private Logger logger = null;

    public GenericJDBCStore() {
        logger = Logger.getLogger(this.getClass().getName());

        // DOM instantiation
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            docBuild = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Handle ParserConfigurationException
            e.printStackTrace();
        }

        try {
            // Open a connection to the store.
            checkConnection();
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Unable to check connection for CentralStore", exc);
        }
    }

    /*
     * @see org.jini.projects.eros.server.ErosBackendStore#log(org.jini.projects.eros.LogDetail)
     */
    public void log(LogDetail logDetail) throws Exception {
        // TODO Complete method stub for log
        try {
            doLog(logDetail);
        } catch (SQLException e) {
            connectionError = true;
            String sqlMessage = "ErrorCode=" + e.getErrorCode() + "\n" + "SQLState=" + e.getSQLState();
            throw new Exception(sqlMessage, e);
        }
    }

    private void doLog(LogDetail logDetail) throws StoreUnavailableException, SQLException {
        checkConnection();
        // String xml = toXML(logDetail);
        // String dbXML = getTransformedXML(xml);
        // // System.out.println("XML to Store:\n" + dbXML);
        HashMap paramMap = new HashMap();
        paramMap.put("ts", logDetail.getDate());
        paramMap.put("instid", new Long(logDetail.getInstanceIdentifier()));
        paramMap.put("msg", logDetail.getMessage());
        paramMap.put("level", logDetail.getLevel().toString());
        paramMap.put("classname", logDetail.getClassName());
        paramMap.put("methodname", logDetail.getMethodName());
        paramMap.put("exceptionname", logDetail.getExceptionName());

        String stackTrunc = logDetail.getStackTrace();
        if (stackTrunc.length() > 1000)
            stackTrunc = stackTrunc.substring(0, 995);

        Matcher m = regex.matcher(stackTrunc);
        int lineValue = -1;
        if (m.find()) {
            String lineDetail = m.group().trim();
            String lineNum = lineDetail.substring(lineDetail.indexOf(":") + 1, lineDetail.length() - 1);
            try {
                lineValue = Integer.parseInt(lineNum);
            } catch (Exception e) {
                // We ignore Number FOrmat Exceptions for STack traces that show
                // Unknown Source
            }
        }
        paramMap.put("linenum", new Integer(lineValue));
        paramMap.put("stack", stackTrunc);
        paramMap.put("args", logDetail.getArguments());
        Map c = columnMap;
        Map p = paramMap;
        String SQL = new String("Insert into " + this.table + "("

        + c.get("time_stamp") + ", " + c.get("inst_id") + ", " + c.get("message") + ", " + c.get("error_level") + ", " + c.get("stacktrace") + ", " + c.get("arguments") + ", " + c.get("classname") + ", " + c.get("methodname") + ","
                + c.get("exceptionname") +"," + c.get("linenum") + ") values (?,?,?,?,?,?,?,?,?,?)");
        // Now get the values out of the paramMap
        // + "'" + paramMap.get("ts") + "', "
        // + "'" + paramMap.get("app") + "', "
        // + "'" + paramMap.get("msg") + "', "
        // + "'" + paramMap.get("level") + "', "
        // + "'" + paramMap.get("stack") + "', "
        // + "'" + paramMap.get("args") + "', "
        // + "'" + paramMap.get("group") + "', "
        // + "'" + paramMap.get("host") + "')"

        long saveStart = System.currentTimeMillis();
        PreparedStatement stmt = conn.prepareStatement(SQL);
        // System.out.println("\n\nWillExecute:\n\n" + SQL + "\n");
        java.util.Date d = (java.util.Date) paramMap.get("ts");
        Timestamp datein = new Timestamp(d.getTime());
        stmt.setTimestamp(1, datein);
        stmt.setLong(2, ((Long) paramMap.get("instid")).longValue());
        stmt.setString(3, (String) paramMap.get("msg"));
        stmt.setString(4, (String) paramMap.get("level"));
        stmt.setString(5, (String) paramMap.get("stack"));
        stmt.setString(6, (String) paramMap.get("args"));
        stmt.setString(7, (String) paramMap.get("classname"));
        stmt.setString(8, (String) paramMap.get("methodname"));
        stmt.setString(9, (String) paramMap.get("exceptionname"));
        stmt.setInt(10, ((Integer) paramMap.get("linenum")).intValue());
        stmt.executeUpdate();
        // sav.insertXML(dbXML);
        conn.commit();
        stmt.close();
        logger.fine("Save took-" + (System.currentTimeMillis() - saveStart) + "ms");
    }

    /*
     * @see org.jini.projects.eros.server.ErosBackendStore#initialise(java.util.logging.Logger,
     *      java.util.Map)
     */
    public void initialise(Logger toConnectTo, Map parameters) throws Exception {
        JDBCHandler jdbcHandler = new JDBCHandler("oracle.jdbc.driver.OracleDriver", getUrl(), getUser(), getPass());
        if (parameters.containsKey("region")) {
            jdbcHandler.setRegion((String[]) parameters.get("region"));
            toConnectTo.addHandler(jdbcHandler);
        } else
            throw new StoreUnavailableException("Store properties not configured correctly");
    }

    /*
     * @see org.jini.projects.eros.server.ErosBackendStore#close()
     */
    public void close() throws Exception {
        // TODO Complete method stub for close
        if (this.conn != null) {
            try {
                conn.close();
            } catch (Exception exc) {
            }
            conn = null;
        }
    }

    private static Node findNode(Node node, String name) {
        if (node.getNodeName().equals(name))
            return node;
        if (node.hasChildNodes()) {
            NodeList nodes = node.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node found = findNode(nodes.item(i), name);
                if (found != null)
                    return found;
            }
        }
        return null;
    }

    /**
     * Checks if the connection to the store is available. Also checks if the
     * file which contains the connection information has changed and reloads it
     * if nesssecary. If no connection is available an attempt to re-connect
     * will be made.
     * 
     * @throws StoreUnavailableException
     *             Any problems establishing a connection with the store.
     */
    private void checkConnection() throws StoreUnavailableException {
        try {
            this.loadConnectionInfo();
            // Check if the current connection is valid and if not
            // re-establish
            // the connection.
            if (conn == null || conn.isClosed() || connectionError) {
                Node driverNode = findNode(document, "DRIVER");
                String driver = driverNode.getFirstChild().getNodeValue();
                Class.forName(driver);
                Node urlNode = findNode(document, "URL");
                url = urlNode.getFirstChild().getNodeValue();
                Node userNode = findNode(document, "USER");
                user = userNode.getFirstChild().getNodeValue();
                Node passwordNode = findNode(document, "PASSWORD");
                if (passwordNode.getFirstChild() != null)
                    pass = passwordNode.getFirstChild().getNodeValue();
                else
                    pass = null;
                Node tableNode = findNode(document, "TABLE");
                table = tableNode.getFirstChild().getNodeValue();
                Node columnNode = findNode(document, "COLUMNS");
                if (columnNode != null) {
                    definedColumns = true;
                    columnMap = new HashMap();
                    NodeList columnList = columnNode.getChildNodes();
                    for (int i = 0; i < columnList.getLength(); i++) {
                        Node column = columnList.item(i);
                        if (column.getNodeType() == Node.ELEMENT_NODE) {
                            String paramName = column.getAttributes().getNamedItem("paramname").getNodeValue();
                            String columnName = column.getAttributes().getNamedItem("columnname").getNodeValue();
                            columnMap.put(paramName, columnName);
                        }
                    }
                }
                logger.info("Checking connection: " + url + ", " + user + "/" + pass);
                conn = DriverManager.getConnection(url, user, pass);

                conn.setAutoCommit(false);

                // Setup the OracleXMLSave object that will be
                // used to save the
                // logging XML.

                connectionError = false;
                logger.fine("New connection established to store.");
            }

        } catch (Exception exc) {
            // Force a reload of connection and transform info next
            // time.
            connectionInfo = null;

            this.closeConnection();
            exc.printStackTrace();
            throw new StoreUnavailableException("Error creating database connection", exc);
        }

    }

    /**
     * Closes the connection to the central store.
     */
    private void closeConnection() {
        if (this.conn != null) {
            try {
                conn.close();
            } catch (Exception exc) {
            }
            conn = null;
        }
    }

    /**
     * Loads the data required to establish a connection to the central store.
     * The data is expected in an XML file format whose location is obtained
     * from the system property <i>org.jini.projects.eros.store.xml </i>.
     * 
     * @throws Exception
     *             Any problems loading or reading the connection data.
     */
    private void loadConnectionInfo() throws Exception {
        if (connectionInfo == null) {
            connectionInfo = new File(System.getProperty("org.jini.projects.eros.store.xml"));
            if (!connectionInfo.isFile() || !connectionInfo.canRead()) {
                throw new Exception("Unable to find or read connection file");
            }
        }
        // Check to see if the connection information file has been
        // modified
        // since the last time we parsed it.
        long lastModified = 0;
        try {
            lastModified = this.connectionInfo.lastModified();
        } catch (Exception ex) {
            // Ignore
        }
        if (lastModified > this.connInfoLastModified) {
            logger.fine("Connection file changed, updating connection.");
            this.document = docBuild.parse(connectionInfo);
            this.connInfoLastModified = lastModified;
            this.closeConnection();
        }
    }

    public static void main(String[] args) {
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public String getTable() {
        return table;
    }

    public long createInstanceRecord(InstanceDetail instanceDetail) throws Exception {
        // TODO Auto-generated method stub
        try {
            checkConnection();
            Statement idMax = conn.createStatement();
            ResultSet rs = idMax.executeQuery("SELECT MAX(id) from eros_instance");
            rs.next();
            long instanceID = rs.getLong(1);
            rs.close();
            idMax.close();
            PreparedStatement stmt = conn.prepareStatement("INSERT into eros_instance (id, application,ipaddress, initialgroups, osname, osversion, osuser, jvmversion) " + "values(?, ?,?,?,?,?,?,?)");
            stmt.setLong(1, instanceID + 1);
            stmt.setString(2, instanceDetail.getApplicationName());
            stmt.setString(3, instanceDetail.getIpAddress());
            stmt.setString(4, instanceDetail.getErosGroups());
            stmt.setString(5, instanceDetail.getOsName());
            stmt.setString(6, instanceDetail.getOsVersion());
            stmt.setString(7, instanceDetail.getOsUser());
            stmt.setString(8, instanceDetail.getJvmVersion());
            stmt.executeUpdate();
            conn.commit();

            stmt.close();
            return instanceID + 1;
        } catch (Exception ex) {
            connectionError = true;
            // ex.printStackTrace();
            return -1;
        }
    }

    public ErrorModel getRecords(java.util.Date from, java.util.Date to) throws Exception {
        // TODO Auto-generated method stub
        // System.out.println("Creating ErrorModel");
        DefaultErrorModel model = new DefaultErrorModel();
        buildInstanceRecords(model);
        buildDataRecords(model, from, to);
        return model;
    }

    private void buildInstanceRecords(DefaultErrorModel model) throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from eros_instance");
        rs.next();

        while (!rs.isAfterLast()) {
            InstanceDetail detail = new InstanceDetailImpl();
            detail.setApplicationName(rs.getString("application"));
            detail.setIpAddress(rs.getString("ipaddress"));
            detail.setErosGroups(rs.getString("initialgroups"));
            detail.setOsName(rs.getString("osname"));
            detail.setOsUser(rs.getString("osuser"));
            detail.setOsVersion(rs.getString("osversion"));
            detail.setJvmVersion(rs.getString("jvmversion"));
            detail.setInstanceIdentifier(rs.getLong("id"));

            rs.next();
            model.addInstanceRecord(detail);
        }
        rs.close();
        stmt.close();

    }

    private void buildDataRecords(DefaultErrorModel model, java.util.Date from, java.util.Date to) throws SQLException {
        if (from == null)
            buildAllDataRecords(model);
        else
            buildRangedDataRecords(model, from, to);
    }

    private void buildRangedDataRecords(DefaultErrorModel model, java.util.Date from, java.util.Date to) throws SQLException {
        // TODO Auto-generated method stub
        PreparedStatement stmt = conn.prepareStatement("select * from eros_error where time_stamp> ? and time_stamp< ? and instancekey >-1");
        Calendar c = new GregorianCalendar();
        c.setTime(from);
        stmt.setTimestamp(1, new Timestamp(c.getTimeInMillis()));
        Calendar c2 = new GregorianCalendar();
        c2.setTime(to);
        c2.add(Calendar.DAY_OF_YEAR, 1);

        stmt.setTimestamp(2, new Timestamp(c2.getTimeInMillis()));

        ResultSet rs = null;

        rs = stmt.executeQuery();

        rs.next();

        while (!rs.isAfterLast()) {
            long instancekey = rs.getLong("instancekey");
            String message = rs.getString("message");
            java.sql.Timestamp t = rs.getTimestamp("time_stamp");
            java.util.Date time_stamp = null;
            if (t != null)
                time_stamp = new java.util.Date(t.getTime());
            else
                time_stamp = new java.util.Date();
            String level = rs.getString("trace_level");
            String stacktrace = rs.getString("stacktrace");
            String arguments = rs.getString("arguments");

            String methodName = rs.getString("methodName");
            String className = rs.getString("className");
            String exceptionName = rs.getString("exceptionName");
            int lineNumber = rs.getInt("linenumber");
            DefaultErrorRecord record = new DefaultErrorRecord(instancekey, message, time_stamp, level, stacktrace, arguments, methodName, className, exceptionName, lineNumber);
            // System.out.println("Loaded Record: " + record);
            rs.next();
            model.addError(record);
        }
        rs.close();
        stmt.close();
    }

    private void buildAllDataRecords(DefaultErrorModel model) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = null;

        rs = stmt.executeQuery("select * from eros_error where instancekey> -1");

        rs.next();

        while (!rs.isAfterLast()) {
            long instancekey = rs.getLong("instancekey");
            String message = rs.getString("message");
            java.sql.Timestamp t = rs.getTimestamp("time_stamp");
            java.util.Date time_stamp = null;
            if (t != null)
                time_stamp = new java.util.Date(t.getTime());
            else
                time_stamp = new java.util.Date();
            String level = rs.getString("trace_level");
            String stacktrace = rs.getString("stacktrace");
            String arguments = rs.getString("arguments");

            String methodName = rs.getString("methodName");
            String className = rs.getString("className");
            String exceptionName = rs.getString("exceptionName");
            int lineNumber = rs.getInt("linenum");
            DefaultErrorRecord record = new DefaultErrorRecord(instancekey, message, time_stamp, level, stacktrace, arguments, methodName, className, exceptionName, lineNumber);
            // System.out.println("Loaded Record: " + record);
            rs.next();
            model.addError(record);
        }
        rs.close();
        stmt.close();
    }

    public InstanceDetail loadInstanceRecord(long id) throws Exception {
        try {
            checkConnection();
            // TODO Auto-generated method stub
            if (id == -1) {
                InstanceDetail detail = new InstanceDetailImpl();
                detail.setApplicationName("UNKNOWN");
                detail.setInstanceIdentifier(-1);
                detail.setErosGroups("UNKNOWN");
                detail.setOsName("UNKNOWN");
                detail.setOsUser("UNKNOWN");
                detail.setOsVersion("UNKNOWN");
                detail.setJvmVersion("UNKNOWN");
                return detail;
            }

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select * from eros_instance where id=" + id);
            rs.next();
            InstanceDetail detail = new InstanceDetailImpl();
            detail.setApplicationName(rs.getString("application"));
            detail.setIpAddress(rs.getString("ipaddress"));
            detail.setErosGroups(rs.getString("initialgroups"));
            detail.setOsName(rs.getString("osname"));
            detail.setOsUser(rs.getString("osuser"));
            detail.setOsVersion(rs.getString("osversion"));
            detail.setJvmVersion(rs.getString("jvmversion"));
            rs.close();
            st.close();
            return detail;
        } catch (SQLException sqlEx) {
            connectionError = true;
        }
        return null;
    }
}
