<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="ListComputers"> Application - Computer Database </a>
        </div>
    </header>
    
    <c:if test="${ fn:length(headerMessage) > 0 }">
        <form id="addedForm" action="ListComputers" method="POST">
            <input type="hidden" value="${ headerMessage }" name="headerMessage"/>
        </form>
        <script type="text/javascript">
            document.getElementById("addedForm").submit()
        </script>
    </c:if>

    <section id="main">
        <div class="container">
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2 box">
                    <c:if test="${ !empty errors }">
                        <div class="alert alert-danger"><ul><c:forEach var="err" items="${ errors }">
                            <li><c:out value="${ err }"></c:out></li>
                        </c:forEach></ul></div>
                    </c:if>
                    <h1>Add Computer</h1>
                    <form:form modelAttribute="computerDto" id="addComputer" action="AddComputer" method="POST">
                        <fieldset>
                        <form:input path="id" type="hidden" value="1"/>
                            <div class="form-group">
                                <form:label path="nom" for="computerName">Computer name</form:label>
                                <div id="errGroup">
	                                <form:input type="text" class="form-control" id="computerName" path="nom" placeholder="Computer name"/>
	                                <div class="addErrText" id="cnErr"></div>
                                </div>
                                
                            </div>
                            <div class="form-group">
                                <form:label path="dateIntroduction"  for="introduced">Introduced date</form:label>
                                <div id="errGroup">
                                    <form:input type="date" class="form-control" id="introduced" path="dateIntroduction" placeholder="Introduced date"/>
                                    <div class="addErrText" id="introErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <form:label path="dateDiscontinuation" for="discontinued">Discontinued date</form:label>
                                <div id="errGroup">
                                    <form:input type="date" class="form-control" id="discontinued" path="dateDiscontinuation" placeholder="Discontinued date"/>
                                    <div class="addErrText" id="discontErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="companyId">Company</label>
                                <select class="form-control" id="companyId" name="companyId" >
                                    <option value="0">--</option>
                                    <c:forEach var="company" items="${ companies }">
                                        <option value="${ company.id }"><c:out value="${ company.nom }"></c:out></option>
                                    </c:forEach> 
                                </select>
                            </div>                  
                        </fieldset> 
                        <div class="actions pull-right">
                            <button type="button" onclick="checkInputs(&quot;addComputer&quot;)" class="btn btn-primary">Add</button>
                            or
                            <a href="ListComputers" class="btn btn-default">Cancel</a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </section>
</body>
</html>