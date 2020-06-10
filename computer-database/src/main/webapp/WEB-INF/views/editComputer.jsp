<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
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
    
    <c:if test="${ editedcomputer == true }">
        <form id="editedForm" action="ListComputers" method="POST">
            <input type="hidden" value="true" name="editedcomputer"/>
        </form>
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
                    <h1>Edit Computer</h1>

                    <form id="editComputer" action="EditComputer" method="POST">
                        <input type="hidden" value="${ id }" id="id"/> <!-- TODO: Change this value with the computer id -->
                        <fieldset>
                            <div class="form-group">
                                <label for="computerName">Computer name</label>
                                <div id="errGroup">
                                    <input type="text" class="form-control" id="computerName" name="computerName" placeholder="Computer name" value="${ computerName }">
                                    <div class="addErrText" id="cnErr"></div>
                                </div>
                                
                            </div>
                            <div class="form-group">
                                <label for="introduced">Introduced date</label>
                                <div id="errGroup">
                                    <input type="date" class="form-control" id="introduced" name="introduced" placeholder="Introduced date" value="${ dateIntro }">
                                    <div class="addErrText" id="introErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="discontinued">Discontinued date</label>
                                <div id="errGroup">
                                    <input type="date" class="form-control" id="discontinued" name="discontinued" placeholder="Discontinued date" value="${ dateDiscont }">
                                    <div class="addErrText" id="discontErr"></div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="companyId">Company</label>
                                <select class="form-control" id="companyId" name="companyId" >
                                    <option value="0">--</option>
                                    <c:forEach var="company" items="${ companies }">
                                        <option value="${ company.id }" <c:if test="${ company.id == companyId }" >selected</c:if>><c:out value="${ company.nom }"></c:out></option>
                                    </c:forEach>
                                </select>
                            </div>                  
                        </fieldset>
                        <div class="actions pull-right">
                            <button type="button" onclick="checkInputs(&quot;editComputer&quot;)" class="btn btn-primary">Edit</button>
                            or
                            <a href="ListComputers" class="btn btn-default">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </section>
</body>
</html>