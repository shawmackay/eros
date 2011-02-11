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

//
///**
// * Title:        Eros Logging Service<p>
// * Description:  Project to provide distributed system logging.<p>
// * Copyright:    Copyright (c) C. Lunn<p>
// * Company:      Countrywide Assured<p>
// * @author C. Lunn
// * @version 1.0
// */
//package org.jini.projects.eros.server;
//
//import java.sql.DriverManager;
//import java.sql.Connection;
//import java.io.File;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.text.SimpleDateFormat;
//import java.util.Map;
//import java.util.logging.Logger;
//import java.util.logging.Level;
//
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.DocumentBuilder;
//
//import org.jini.projects.eros.LogDetail;
//import org.jini.projects.eros.exception.StoreUnavailableException;
//import org.jini.projects.eros.logging.JDBCHandler;
//import org.w3c.dom.*;
//import javax.xml.transform.stream.StreamSource;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//
//import oracle.xml.sql.OracleXMLSQLException;
//import oracle.xml.sql.dml.OracleXMLSave;
//
///**
// * Controller class that handles the persitent store of the log details into
// * a central store.
// *
// * All errors logged with Eros are passed to this class to be persisted.
// * <p>
// * The persistent store used by this class is currently an Oracle database.
// * The data is stored using XML & XSL to manipulate the data quickly and 
// * simplify the data load into the database.
// * <p>
// * This class is dependant on two files being available, whose location is
// * specified via system properties.
// * <ul>
// * <li>
// * <code>uk.co.cwa.eros.store.xml</code>
// *  - This file provides the connection information
// * for the persistant store for this Jini group.
// * </li>
// * <li>
// * <code>uk.co.cwa.eros.transform.xml</code>
// *  - This <i>XSL</i> file should provide any
// * transformations required to convert the <code>LogDetail</code> standard
// * XML into the format required for the persistant store.
// * </li>
// * </ul>
// */
//public class CentralStore implements ErosBackendStore{
//    /**
//     * The date format that is used when a string representation of any date
//     * from the logging detail, eg 24-12-2002 23:59:59.
//     */
//    public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
//    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
//
//    private String url = "";
//    private String user = "";
//    private String pass = "";
//    private String table = "";
//        
//    private Connection conn = null;
//    private Logger logger = null;
//   
//    private boolean connectionError = false;
//    private TransformerFactory tFactory = null;
//    private Transformer transformer = null;
//    private File connectionInfo = null;
//    private long connInfoLastModified = 0;
//    private long transInfoLastModified = 0;
//    private Document document = null;    
//    private DocumentBuilder docBuild  = null;
//    private File transformInfo = null;
//    private OracleXMLSave sav = null;
//
//    /**
//     * Trys to establish a connection to the central store.
//     * Initialises some of the components required for XML processing.
//     * @throws Exception Any problems with the store or XML components.
//     */
//    public CentralStore() throws Exception {
//        logger = Logger.getLogger(this.getClass().getName());
//        Class.forName("oracle.jdbc.driver.OracleDriver");
//
//        // DOM instantiation
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        docBuild = dbf.newDocumentBuilder();                
//        //Transformer Initialisation
//        tFactory = javax.xml.transform.TransformerFactory.newInstance();
//        try {
//            //Open a connection to the store.
//            checkConnection();
//        } catch (Exception exc) {
//            logger.log(Level.WARNING, "Unable to check connection for CentralStore",exc);
//        }
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public String getUser() {
//        return user;
//    }
//
//    public String getPass() {
//        return pass;
//    }
//
//    public String getTable() {
//        return table;
//    }
//
//    /**
//     * Attempts to log the detail with the store.
//     * @param logDetail The logging detail that should be stored centrally.
//     * @throws Exception Any exceptions in extracting the logging data
//     *          or storing the data.
//     */
//    public void log(LogDetail logDetail) throws Exception {
//        try {            
//            
//            doLog(logDetail);            
//
//        } catch (java.sql.SQLException sqlExc) {
//            connectionError = true;
//            String sqlMessage = "ErrorCode=" +
//                                sqlExc.getErrorCode() +
//                                "\n" +
//                                "SQLState=" +
//                                sqlExc.getSQLState();
//            throw new Exception(sqlMessage, sqlExc);
//        } catch (OracleXMLSQLException xmlSqlExc) {
//            connectionError = true;
//            String sqlMessage = "XML Error in central store, ErrorCode=" +
//                                xmlSqlExc.getErrorCode() +
//                                ", " +
//                                "XMLError=" +
//                                xmlSqlExc.getXMLErrorString();
//            throw new Exception(sqlMessage, xmlSqlExc);
//        } catch (StoreUnavailableException storeExc) {
//            throw storeExc;            
//        } catch (Exception exc) {
//            throw new Exception("Unable to log with central store", exc);
//        } finally {
//            System.gc();
//        }
//        
//    }
//
//    /**
//     * Checks if the connection to the store is available.
//     * Also checks if the file which contains the connection information
//     * has changed and reloads it if nesssecary.
//     * If no connection is available an attempt to re-connect will be made.
//     * @throws StoreUnavailableException
//     *          Any problems establishing a connection with the store.
//     */
//    private void checkConnection() throws StoreUnavailableException {
//        try {
//            this.loadConnectionInfo();
//            //Check if the current connection is valid and if not re-establish
//            //the connection.            
//            if( conn == null || conn.isClosed() || connectionError ) {
//                Node urlNode = findNode(document,"URL");
//                url = urlNode.getFirstChild().getNodeValue();
//                Node userNode = findNode(document,"USER");
//                user = userNode.getFirstChild().getNodeValue();
//                Node passwordNode = findNode(document,"PASSWORD");
//                pass = passwordNode.getFirstChild().getNodeValue();
//                Node tableNode = findNode(document,"TABLE");
//                table = tableNode.getFirstChild().getNodeValue();
//                logger.info("Checking connection: " + url + ", " + user  + "/" + pass);
//                conn = DriverManager.getConnection(url, user, pass);
//               
//                conn.setAutoCommit(false);
//                
//                //Setup the OracleXMLSave object that will be used to save the
//                //logging XML.
//                if( sav != null ) {
//                    try {                        
//                        sav.close();
//                        sav = null;
//                    } catch (Exception exc) {
//                        logger.log(Level.INFO, "Error closing OracleXMLSave", exc);
//                    }
//                }
//                                
//                sav = new OracleXMLSave(conn, table);
//                sav.setDateFormat(DATE_FORMAT);
//                connectionError = false;
//                logger.fine("New connection established to store.");
//            }
//            this.loadTransformInfo();
//
//        } catch (Exception exc) {
//            //Force a reload of connection and transform info next time.
//            connectionInfo = null;
//            transformInfo = null;
//            transformer = null;
//            this.closeConnection();
//            exc.printStackTrace();
//            throw new StoreUnavailableException(
//                            "Error creating Oracle connection", exc);
//        }
//
//    }
//
//    /**
//     * Loads the data required to establish a connection to the central store.
//     * The data is expected in an XML file format whose location is obtained 
//     * from the system property <i>org.jini.projects.eros.store.xml</i>.
//     * @throws Exception Any problems loading or reading the connection data.
//     */
//    private void loadConnectionInfo() throws Exception {
//        if( connectionInfo == null) {
//            connectionInfo = new File(System.getProperty("org.jini.projects.eros.store.xml"));        
//            if( !connectionInfo.isFile() || !connectionInfo.canRead() ) {
//                throw new Exception("Unable to find or read connection file");
//            }
//        }
//        //Check to see if the connection information file has been modified
//        //since the last time we parsed it.
//        long lastModified = 0;
//        try {
//            lastModified = this.connectionInfo.lastModified();
//        } catch (Exception ex) {
//            //Ignore
//        }                        
//        if(lastModified > this.connInfoLastModified)  {
//            logger.fine("Connection file changed, updating connection.");
//            this.document = docBuild.parse(connectionInfo);
//            this.connInfoLastModified = lastModified;
//            this.closeConnection();
//        }        
//    }
//    
//    /**
//     * Loads the data required to transform the incoming data to a suitable
//     * format for the central store.<br>
//     * The data is expected in an XML file format whose location is obtained 
//     * from the system property <i>org.jini.projects.eros.transform.xml</i>.
//     * @throws Exception Any problems loading or reading the transform data.
//     */
//    private void loadTransformInfo() throws Exception {
//        if( transformInfo == null ) {
//            transformInfo = new File(System.getProperty("org.jini.projects.eros.transform.xml"));
//            if( !transformInfo.isFile() || !transformInfo.canRead() ) {
//                throw new Exception("Unable to find or read transformation file");
//            }            
//        }
//
//        //Check to see if the transform information file has been modified
//        //since the last time we parsed it.
//        long lastModified = 0;
//        try {
//            lastModified = this.transformInfo.lastModified();
//        } catch (Exception ex) {
//            //Ignore
//        }                        
//        if(transformer == null || lastModified > this.transInfoLastModified)  {               
//            logger.fine("Transformation file changed, updating.");
//            this.transInfoLastModified = lastModified;
//            
//            StreamSource source = new StreamSource(transformInfo);
//            transformer = tFactory.newTransformer(source);
//            transformer.setErrorListener(
//                new org.apache.xml.utils.DefaultErrorHandler() {
//                    public void fatalError(org.xml.sax.SAXParseException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println("Line=" + exception.getLineNumber());
//                        System.out.println("Column=" + exception.getColumnNumber());
//                    }
//                    public void fatalError(javax.xml.transform.TransformerException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println(exception.getLocationAsString());
//                        System.out.println(exception.getMessageAndLocation());                    
//                    }
//                    public void error(org.xml.sax.SAXParseException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println("Line=" + exception.getLineNumber());
//                        System.out.println("Column=" + exception.getColumnNumber());
//                    }
//                    public void error(javax.xml.transform.TransformerException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println(exception.getLocationAsString());
//                        System.out.println(exception.getMessageAndLocation());                    
//                    }
//                    public void warning(org.xml.sax.SAXParseException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println("Line=" + exception.getLineNumber());
//                        System.out.println("Column=" + exception.getColumnNumber());
//                    }
//                    public void warning(javax.xml.transform.TransformerException exception) {
//                        logger.log(Level.WARNING, "Error transforming incoming XML", exception);
//                        exception.printStackTrace();
//                        System.out.println(exception.getLocationAsString());
//                        System.out.println(exception.getMessageAndLocation());                    
//                    }
//
//                }
//            );
//        }
//    }
//
//    /**
//     * Closes the connection to the central store.
//     */
//    private void closeConnection() {
//        if( this.conn != null) {
//            try{
//                conn.close();
//            } catch (Exception exc) {
//            }
//            conn = null;
//        }
//    }
//
//    /**
//     * Internal logging method.
//     */
//    private void doLog(LogDetail logDetail) throws Exception {
//        checkConnection();            
//        String xml = toXML(logDetail);
//        String dbXML = getTransformedXML(xml);
//       // System.out.println("XML to Store:\n" + dbXML);
//        long saveStart = System.currentTimeMillis();
//        sav.insertXML(dbXML);
//        conn.commit();
//        logger.fine("Save took-" + (System.currentTimeMillis()-saveStart) + "ms");
//    }
//
//    private String getTransformedXML(String xml) throws TransformerException {
//        StringReader sr = new StringReader(xml);
//        StringWriter sw = new StringWriter();
//        long transformStart = System.currentTimeMillis();
//        transformer.transform(new StreamSource(sr), new StreamResult(sw));
//        logger.fine("XML Transform took-" + (System.currentTimeMillis()-transformStart) + "ms");
//        String dbXML = sw.toString();
//        try {
//            sr.close();
//            sw.close();
//        } catch (Exception exc) {
//            logger.log(Level.WARNING, "Error closing streams for XML transform", exc);
//        }
//        return dbXML;
//    }
//
//
//    /**
//     * Closes the connection to the central store.
//     */
//    public void close() throws Exception{
//        this.closeConnection();
//    }
//    
//    private static Node findNode(Node node, String name) {
//        if( node.getNodeName().equals(name) )
//            return node;
//        if( node.hasChildNodes() ) {
//            NodeList nodes = node.getChildNodes();
//            for(int i=0; i < nodes.getLength(); i++) {
//                Node found = findNode(nodes.item(i), name);
//                if( found != null)
//                    return found;
//            }
//        }
//        return null;
//    }
//
//    /**
//    private static String getAttribute(Node node, String name) {
//        if( node instanceof Element ) {
//            Element element = (Element) node;
//            return element.getAttribute(name);
//        }
//        return null;
//    }
//    */
//
//    /**
//     * Provides an XML representation of the data held within the object.
//     * A <code>String</code> is generated that contains a valid XML structure.
//     * An example is shown below : <br>
//     *  <?xml version="1.0"?>
//     *  <LOGGINGDETAIL>
//     *  <DATE>20-03-2002 04:36:20</DATE>
//     *  <CODE>0</CODE>
//     *  <MESSAGE>Unknown</MESSAGE>
//     *  <STACKTRACE>Unknown</STACKTRACE>
//     *  <APPLICATION>Unknown</APPLICATION>
//     *  <ARGUMENTS>None</ARGUMENTS>
//     *  <GROUPS>Unknown</GROUPS>
//     *  <HOSTNAME>e0144sts2s</HOSTNAME>
//     *  <HOSTADDRESS>193.32.103.224</HOSTADDRESS>
//     *  <OPERATINGSYSTEM>Windows NT; x86; 4.0</OPERATINGSYSTEM>
//     *  <JAVAVERSION>1.4.0</JAVAVERSION>
//     *  <USERNAME>Chrisl</USERNAME>
//     *  <LEVEL>1</LEVEL>
//     *  </LOGGINGDETAIL>
//     *
//     * @return A <code>String</code> representation of an XML structure of
//     * the data held within the object.
//     */
//    public static String toXML(LogDetail logDetail) {
//        StringBuffer sb = new StringBuffer();
//        sb.append("<?xml version=\"1.0\"?>\n");
//        sb.append("<LOGGINGDETAIL>\n");
//        sb.append("<DATE>");
//        sb.append(sdf.format(logDetail.getDate()));
//        sb.append("</DATE>\n");
//        sb.append("<CODE>");
//        sb.append(logDetail.getCode().toString());
//        sb.append("</CODE>\n");
//        sb.append("<MESSAGE>");
//        sb.append(logDetail.getMessage());
//        sb.append("</MESSAGE>\n");
//        sb.append("<STACKTRACE>");
//        sb.append(logDetail.getStackTrace());
//        sb.append("</STACKTRACE>\n");
//        sb.append("<APPLICATION>");
//        sb.append(logDetail.getApplicationName());
//        sb.append("</APPLICATION>\n");
//        sb.append("<ARGUMENTS>");
//        sb.append(logDetail.getArguments());
//        sb.append("</ARGUMENTS>\n");
//        sb.append("<GROUPS>");
//        sb.append(logDetail.getGroups());
//        sb.append("</GROUPS>\n");
//        sb.append("<HOSTNAME>");
//        sb.append(logDetail.getHostName());
//        sb.append("</HOSTNAME>\n");
//        sb.append("<HOSTADDRESS>");
//        sb.append(logDetail.getHostAddress());
//        sb.append("</HOSTADDRESS>\n");
//        sb.append("<OPERATINGSYSTEM>");
//        sb.append(logDetail.getOSDetails());
//        sb.append("</OPERATINGSYSTEM>\n");
//        sb.append("<JAVAVERSION>");
//        sb.append(logDetail.getJavaVersion());
//        sb.append("</JAVAVERSION>\n");
//        sb.append("<USERNAME>");
//        sb.append(logDetail.getUserName());
//        sb.append("</USERNAME>\n");
//        sb.append("<LEVEL>");
//        sb.append(logDetail.getLevel());
//        sb.append("</LEVEL>\n");
//        sb.append("</LOGGINGDETAIL>");
//        return sb.toString();
//    }
//
//    /**
//     * Provides a simple string representation of the store details.
//     * This consists of the database URL, the database user and the
//     * configuration file that is being used.
//     * @return The store details.
//     */
//    public String toString() {
//        return  "URL : " + this.url + "\n" +
//                "User : " + this.user + "\n" +
//                "Config : " + System.getProperty("org.jini.projects.eros.store.xml");
//    }
//
//    public static void main(String[] args) throws Exception {
//        CentralStore cs = null;
//        try {
//            System.setProperty("org.jini.projects.eros.transform.xml",
//                                    "d:\\jbproject\\eros2\\ErrorTable2.xsl");
//            System.setProperty("org.jini.projects.eros.store.xml",
//                                    "d:\\jbproject\\eros2\\src\\conninfo_development.xml");
//
//            cs = new CentralStore();
//            System.out.println(cs.toString());
//
//            org.jini.projects.eros.LoggingDetail ld = new org.jini.projects.eros.LoggingDetail();
//            ld.setApplicationName("CentralStore Test");
//            ld.setArguments(new Object[]{"FirstObj", new Integer(1)});
//            ld.setCode(new Integer(99));
//            ld.setDate(new java.util.Date());
//            ld.setGroups(new String[]{"testing"});
//            ld.setLevel(org.jini.projects.eros.LogLevel.INFO);
//            ld.setMessage("This is a test for the central store class.");
//
//            try {
//                new java.util.Vector().get(1);
//            } catch (Exception e) {
//                StringWriter sw;
//                sw = new java.io.StringWriter();
//                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
//                e.printStackTrace(pw);
//                ld.setStackTrace(sw.toString());
//                pw.close();
//            }
//
//            String xml = cs.toXML(ld);
//            String dbXML = cs.getTransformedXML(xml);
//            System.out.println(dbXML);
//
//            //cs.log(ld);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cs.close();
//        }
//    }
//
//	/* @see org.jini.projects.eros.server.ErosBackendStore#initialise(java.util.logging.Logger, java.util.Map)
//	 */
//	public void initialise(Logger toConnectTo, Map parameters) throws Exception {
//        JDBCHandler jdbcHandler = new JDBCHandler("oracle.jdbc.driver.OracleDriver", getUrl(), getUser(), getPass());
//        if(parameters.containsKey("region")){
//        jdbcHandler.setRegion((String[]) parameters.get("region"));        
//        toConnectTo.addHandler(jdbcHandler);
//        } else
//            throw new StoreUnavailableException("Store properties not configured correctly");
//	}
//}
