<%--
  Created by IntelliJ IDEA.
  User: sadnan
  Date: 3/9/24
  Time: 8:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String hostName = request.getServerName();
%>
<html>
<head>
    <title>NorthWykeFarmPlatform API Endpoint SADI Services</title>
</head>
<body>
<div id='outer-frame'>
    <div id='inner-frame'>
        <div id='header'>
            <h1>SADI services at <%=hostName%></h1>
        </div>
        <div id='nav'>
            <ul>
                <li>Services</li>
            </ul>
        </div>
        <div id='content'>
            <h2>SADI Services</h2>
            <ul>
                <li><a href="./getCatchment">getCatchment</a></li>
                <li><a href="./getField">getField</a></li>
                <li><a href="./getAnimalBasicData">getAnimalBasicData</a></li>
            </Ul>
        </div> <!-- content -->
        <div id='footer'>
        </div> <!-- footer -->
    </div> <!-- inner-frame -->
</div> <!-- outer-frame -->
</body>
</html>
