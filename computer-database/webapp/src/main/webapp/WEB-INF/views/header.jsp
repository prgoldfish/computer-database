<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "spring" uri = "http://www.springframework.org/tags" %>
<%@ taglib prefix = "security" uri = "http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
	<head>
        <script src="./js/common.js"></script>    
	</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="ListComputers"><spring:message code="header.name"/> </a>
            <div class="btn btn-info login">
	            <security:authorize access="isAuthenticated()">
	                <a href=<spring:url value="logout"/>><spring:message code="logout"></spring:message></a>
	                <c:set var="authenticated" value="true"/>
	            </security:authorize>
	            <c:if test="${authenticated != 'true'}">
	               <a href=<spring:url value="login"/>><spring:message code="login"></spring:message></a>
	            </c:if>
            </div>
            <div class="rightContainer">
                <a href="#" onclick="setLang(&quot;fr&quot;)"><img alt="French/Français" src="./images/french.png" height="32"></a>
                <a href="#" onclick="setLang(&quot;en&quot;)"><img alt="English/Anglais" src="./images/english.png" height="32"></a>
            </div>
        </div>
    </header>
</body>
</html>