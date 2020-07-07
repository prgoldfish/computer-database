<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
	<title>Computer Database</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!-- Bootstrap -->
	<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="./css/font-awesome.css" rel="stylesheet" media="screen">
	<link href="./css/main.css" rel="stylesheet" media="screen">
	<script type="text/javascript" src="./js/jquery.min.js"></script>
	<script type="text/javascript" src="./js/addComputer.js"></script>
    <script src="./js/common.js"></script>
</head>
<body>
    <form id="langForm" action="#" method="GET">
        <input type="hidden" name="id" value="${ id }"/>
        <input type="hidden" name="lang" value=""/>
    </form>
    
    <jsp:include page="header.jsp"/>
        
    <script type="text/javascript">
        let strings = new Array();
        strings['js.computer.name.empty'] = "<spring:message code='js.computer.name.empty' javaScriptEscape='true' />";
        strings['js.date.discont.without.intro'] = "<spring:message code='js.date.discont.without.intro' javaScriptEscape='true' />";
        strings['js.date.discont.before.intro'] = "<spring:message code='js.date.discont.before.intro' javaScriptEscape='true' />";
        strings['js.fields.invalid'] = "<spring:message code='js.date.discont.before.intro' javaScriptEscape='true' />";
    </script>
    
    <c:if test="${ fn:length(headerMessage) > 0 }">
        <form:form modelAttribute="dashboardDTO" id="editedForm" action="ListComputers" method="POST">
            <form:input path="headerMessage" type="hidden" value="${ headerMessage }" name="headerMessage"/>
        </form:form>
        <script type="text/javascript">
            document.getElementById("editedForm").submit()
        </script>
    </c:if>
    
    <section id="main">
        <div class="container">
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2 box">
                    <div class="label label-default pull-right">
                        id: <c:out value="${ id }"></c:out>
                    </div>
                    <h1><spring:message code="editComputer.title"/></h1>

                    <form:form modelAttribute="computerDto" id="editComputer" action="EditComputer" method="POST">
                        <form:input type="hidden" path="id" value="${ id }" name="id" id="id"/>
                        <fieldset>
                            <div class="form-group">
                                <form:label path="nom" for="computerName"><spring:message code="computerName"/></form:label>
                                <div id="errGroup">
                                    <form:input type="text" class="form-control" id="computerName" path="nom" name="computerName" value="${ computerName }" placeholder="Computer name"/>
                                    <div class="addErrText" id="cnErr"></div>
                                </div>
                                
                            </div>
                            <div class="form-group">
                                <form:label path="dateIntroduction"  for="introduced"><spring:message code="date.introduced"/></form:label>
                                <div id="errGroup">
                                    <form:input type="date" class="form-control" id="introduced" path="dateIntroduction" nam="introduced" value="${ dateIntro }" placeholder="Introduced date"/>
                                    <div class="addErrText" id="introErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <form:label path="dateDiscontinuation" for="discontinued"><spring:message code="date.discontinued"/></form:label>
                                <div id="errGroup">
                                    <form:input type="date" class="form-control" id="discontinued" path="dateDiscontinuation" name="discontinued" value="${ dateDiscont }" placeholder="Discontinued date"/>
                                    <div class="addErrText" id="discontErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="companyId"><spring:message code="company"/></label>
                                <select class="form-control" id="companyId" name="companyId" >
                                    <option value="0">--</option>
                                    <c:forEach var="company" items="${ companies }">
                                        <option value="${ company.id }" <c:if test="${ company.id == companyId }" >selected</c:if>><c:out value="${ company.name }"></c:out></option>
                                    </c:forEach>
                                </select>
                            </div>                  
                        </fieldset>
                        <div class="actions pull-right">
                            <button type="button" onclick="checkInputs(&quot;editComputer&quot;)" class="btn btn-primary"><spring:message code="edit"/></button>
                            <spring:message code="or"/>
                            <a href="ListComputers" class="btn btn-default"><spring:message code="cancel"/></a>
                        </div> 
                    </form:form>
                </div>
            </div>
        </div>
    </section>
</body>
</html>