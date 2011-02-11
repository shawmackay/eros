<%@ page import="org.jini.projects.eros.jsp.ErosBean"%>
<jsp:useBean id="erosBean" scope="session" class="org.jini.projects.eros.jsp.ErosBean"/>
<html>
    <head>
        <title>Eros Error Viewer</title>
    </head>
    <body>
        <h1>Errors Logged by Eros</h1>
	    <p>
	    <a href="issues.html" target="_blank">Click here for the list of known issues.</a>
	    </p>

        <%
            String DEFAULT_TEXT_COLOR = "black";
            String TODAY_TEXT_COLOR = "red";
            String YESTERDAY_TEXT_COLOR = "green";
            String PREVIOUS_TEXT_COLOR = "purple";

            boolean showAll = false;
            if(request.getParameter("showAll") != null) {
                showAll = true;
            }

		    erosBean.setDBUrl("jdbc:oracle:thin:@nts4_006.countrywide-assured.co.uk:1521:SSDB");
            erosBean.setRegion("production");
            erosBean.refreshData(showAll);

            out.println("<table border=\"2\">");
            out.println("<tr bgcolor=\"gray\">");
            out.println("<th>Date</th><th>Application</th><th>Arguments</th>" +
                        "<th>Exception</th>");
            String color = "black";
            while( erosBean.hasNext() ) {
                switch( erosBean.getDay() ) {
                    case ErosBean.TODAY :
                        if( !color.equals(TODAY_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\" bgcolor=\"" +
                                    TODAY_TEXT_COLOR + "\"><b>Errors Today</b></td></tr>");
                        }
                        color = TODAY_TEXT_COLOR;
                        break;
                    case ErosBean.YESTERDAY :
                        if( !color.equals(YESTERDAY_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\" bgcolor=\"" +
                                    YESTERDAY_TEXT_COLOR + "\"><b>Errors Yesterday</b></td></tr>");
                        }
                        color = YESTERDAY_TEXT_COLOR;
                        break;
                    case ErosBean.PREVIOUSDAY :
                        if( !color.equals(PREVIOUS_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\" bgcolor=\"" +
                                    PREVIOUS_TEXT_COLOR + "\"><b>Errors Previous Day</b></td></tr>");
                        }
                        color = PREVIOUS_TEXT_COLOR;
                        break;
                    default:
                        if( !color.equals(DEFAULT_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\"><b>Old Errors</b></td></tr>");
                        }
                        color = DEFAULT_TEXT_COLOR;
                }
                out.println("<tr bgcolor=\"white\">");
                out.println("<td width=\"15%\"><font color=\"" + color + "\">" +
                                erosBean.getTimeStamp() + "</font></td>");
                out.println("<td width=\"15%\"><font color=\"" + color + "\">" +
                                erosBean.getApplication() + "</font></td>");
                out.println("<td width=\"35%\"><font color=\"" + color + "\">" +
                                erosBean.getArguments() + "</font></td>");
                out.println("<td width=\"35%\"><font color=\"" + color + "\">" +
                                erosBean.getException() + "</font></td>");
                out.println("</tr>");
            }
            out.println("</table>");

            erosBean.close();

            out.println("<p>");
            if(showAll) {
	            out.println("<a href=\"ErosViewer.jsp\" target=\"_top\">Click here to hide system errors.</a>");
            } else {
	            out.println("<a href=\"ErosViewer.jsp?showAll=true\" target=\"_top\">Click here to show system errors.</a>");
            }
            out.println("</p>");
        %>
    </body>
</html>