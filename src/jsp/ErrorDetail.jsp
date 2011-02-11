<%@ page import="org.blarty.eros.jsp.ErrorBean"%>
<jsp:useBean id="errorBean" scope="session" class="org.blarty.eros.jsp.ErrorBean"/>
<html>
    <head>
        <title>Error Detail View</title>
    </head>
    <body>
        <h1>Error Detail View</h1>
        <% errorBean.getErrorDetail(Integer.parseInt(request.getParameter("id"))); %>
        <table border="1">
            <tr bgcolor="silver">
                <th width="50%">TimeStamp</th>
                <th width="50%">Groups</th>
            </tr>
            <tr>
                <td width="50%"><% out.println(errorBean.getTimeStamp()); %></td>
                <td width="50%"><% out.println(errorBean.getGroups()); %></td>
            </tr>
            <tr bgcolor="silver">
                <th width="50%">Application</th>
                <th width="50%">Arguments</th>
            </tr>
            <tr>
                <td width="50%"><% out.println(errorBean.getApplication()); %></td>
                <td width="50%"><% out.println(errorBean.getArguments()); %></td>
            </tr>
            <tr bgcolor="silver">
                <th width="50%">Level</th>
                <th width="50%">Host</th>
            </tr>
            <tr>
                <td width="50%"><% out.println(errorBean.getLevel()); %></td>
                <td width="50%"><% out.println(errorBean.getHost()); %></td>
            </tr>
            <tr bgcolor="silver">
                <th colspan="3" align="left">Message</th>
            </tr>
            <tr>
                <td colspan="3"><% out.println(errorBean.getMessage()); %></td>
            </tr>
            <tr bgcolor="silver">
                <th colspan="3" align="left">StackTrace</th>
            </tr>
            <tr>
                <td colspan="3"><% out.println(errorBean.getStackTrace()); %></td>
            </tr>
    </body>
</html>