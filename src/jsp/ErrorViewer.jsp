<%@ page import="org.blarty.eros.jsp.ErrorBean"%>
<jsp:useBean id="errorBean" scope="session" class="org.blarty.eros.jsp.ErrorBean"/>
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
            String showAllParam = "";
            if(request.getParameter("showAll") != null) {
                showAll = true;
                showAllParam = "showAll=true&";
            }
            int sortCol = ErrorBean.TIME_COL;
            if(request.getParameter("sortCol") != null) {
                sortCol = Integer.parseInt(request.getParameter("sortCol"));
            }
            boolean ascend = false;
            if(request.getParameter("ascend") != null) {
                ascend = Boolean.valueOf(request.getParameter("ascend")).booleanValue();
            }
		    errorBean.setDBUrl("jdbc:oracle:thin:@nts4_005.countrywide-assured.co.uk:1521:SSDT");
            errorBean.setRegion("production");
            errorBean.refreshData(showAll, sortCol, ascend);

            out.println("<table border=\"2\" width=\"100%\">");
            out.println("<tr bgcolor=\"silver\">");
            out.println("<th width=\"20%\">Date" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.TIME_COL + "&ascend=true>" +
                                "<img src=\"Up16.gif\" alt=\"Ascending\" align=\"right\"/>" +
                            "</a>" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.TIME_COL + "&ascend=false>" +
                                "<img src=\"Down16.gif\" alt=\"Descending\" align=\"right\"/>" +
                            "</a>" +
                        "</th>" +
                        "<th width=\"20%\">Application" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.APPLICATION_COL + "&ascend=true>" +
                                "<img src=\"Up16.gif\" alt=\"Ascending\" align=\"right\"/>" +
                            "</a>" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.APPLICATION_COL + "&ascend=false>" +
                                "<img src=\"Down16.gif\" alt=\"Descending\" align=\"right\"/>" +
                            "</a>" +
                        "</th>" +
                        "<th width=\"10%\">Level" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.LEVEL_COL + "&ascend=true>" +
                                "<img src=\"Up16.gif\" alt=\"Ascending\" align=\"right\"/>" +
                            "</a>" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.LEVEL_COL + "&ascend=false>" +
                                "<img src=\"Down16.gif\" alt=\"Descending\" align=\"right\"/>" +
                            "</a>" +
                        "</th>" +
                        "<th width=\"50%\">Message" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.MESSAGE_COL + "&ascend=true>" +
                                "<img src=\"Up16.gif\" alt=\"Ascending\" align=\"right\"/>" +
                            "</a>" +
                            "<a href=ErrorViewer.jsp?" + showAllParam + "sortCol=" +
                                ErrorBean.MESSAGE_COL + "&ascend=false>" +
                                "<img src=\"Down16.gif\" alt=\"Descending\" align=\"right\"/>" +
                            "</a>" +
                        "</th>");
            String color = "black";
            while( errorBean.hasNext() ) {
                switch( errorBean.getDay() ) {
                    case ErrorBean.TODAY :
                        if( !color.equals(TODAY_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\" bgcolor=\"" +
                                    TODAY_TEXT_COLOR + "\"><b>Errors Today</b></td></tr>");
                        }
                        color = TODAY_TEXT_COLOR;
                        break;
                    case ErrorBean.YESTERDAY :
                        if( !color.equals(YESTERDAY_TEXT_COLOR) ) {
                            out.println("<tr><td colspan=\"4\" bgcolor=\"" +
                                    YESTERDAY_TEXT_COLOR + "\"><b>Errors Yesterday</b></td></tr>");
                        }
                        color = YESTERDAY_TEXT_COLOR;
                        break;
                    case ErrorBean.PREVIOUSDAY :
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
                out.println("<td><font color=\"" + color + "\">" +
                                "<a href=ErrorDetail.jsp?id=" + errorBean.getId() + ">"
                                + errorBean.getTimeStamp() + "</a>" + "</font></td>");
                out.println("<td><font color=\"" + color + "\">" +
                                errorBean.getApplication() + "</font></td>");
                out.println("<td><font color=\"" + color + "\">" +
                                errorBean.getLevel() + "</font></td>");
                out.println("<td><font color=\"" + color + "\">" +
                                errorBean.getMessage() + "</font></td>");
                out.println("</tr>");
            }
            out.println("</table>");

            errorBean.close();

            out.println("<p>");
            if(showAll) {
	            out.println("<a href=\"ErrorViewer.jsp\" target=\"_top\">Click here to hide system errors.</a>");
            } else {
	            out.println("<a href=\"ErrorViewer.jsp?showAll=true\" target=\"_top\">Click here to show system errors.</a>");
            }
            out.println("</p>");
        %>
    </body>
</html>