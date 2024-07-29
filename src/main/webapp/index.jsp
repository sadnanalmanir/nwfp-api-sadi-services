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
                <li><a href="./allCatchments">allCatchments</a></li>
                <li><a href="./getCatchmentInfo">getCatchmentInfo</a></li>
                <li><a href="./allCatchmentMeasurementTypes">allCatchmentMeasurementTypes</a></li>
                <li><a href="./getCatchmentMeasurementTypeInfo">getCatchmentMeasurementTypeInfo</a></li>
                <li><a href="./allFields">allFields</a></li>
                <li><a href="./getFieldInfo">getFieldInfo</a></li>
                <li><a href="./allFieldEvents">allFieldEvents</a></li>
                <li><a href="./getFieldEventInfo">getFieldEventInfo</a></li>
                <li><a href="./allAnimalBasicData">allAnimalBasicData</a></li>
                <li><a href="./getAnimalBasicDataInfo">getAnimalBasicDataInfo</a></li>
                <li><a href="./allDataQualities">allDataQualities</a></li>
                <li><a href="./getDataQualityInfo">getDataQualityInfo</a></li>
                <li><a href="./allMeasurementLocations">allMeasurementLocations</a></li>
                <li><a href="./getMeasurementLocationInfo">getMeasurementLocationInfo</a></li>
                <li><a href="./allMeasurementTypes">allMeasurementTypes</a></li>
                <li><a href="./getMeasurementTypeInfo">getMeasurementTypeInfo</a></li>
                <li><a href="./allMeasurementTypesLong">allMeasurementTypesLong</a></li>
                <li><a href="./getMeasurementTypeLongInfo">getMeasurementTypeLongInfo</a></li>
                <li><a href="./getMeasurementByCatchmentName">getMeasurementByCatchmentName</a></li>
                <li><a href="./getMeasurementByDateRange">getMeasurementByDateRange</a></li>
                <li><a href="./getMeasurementByTypeId">getMeasurementByTypeId</a></li>
            </Ul>
        </div> <!-- content -->
        <div id='footer'>
        </div> <!-- footer -->
    </div> <!-- inner-frame -->
</div> <!-- outer-frame -->
</body>
</html>
