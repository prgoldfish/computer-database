<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  

<!DOCTYPE html>
<html>
<head>
	<title>Computer Database</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta charset="utf-8">
	<!-- Bootstrap -->
	<link href="./css/bootstrap.min.css" rel="stylesheet" media="screen">
	<link href="./css/font-awesome.css" rel="stylesheet" media="screen">
	<link href="./css/main.css" rel="stylesheet" media="screen">
	<script src="./js/jquery.min.js"></script>
	<script src="./js/bootstrap.min.js"></script>
	<script src="./js/dashboard.js"></script>
</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="ListComputers"> Application - Computer Database </a>
        </div>
    </header>

    <section id="main">
        <div class="container">
        <c:if test="${ fn:length(headerMessage) > 0 }">
           <div class="alert alert-danger">
              <c:out value="${ headerMessage }"></c:out> <br/>
          </div>
        </c:if>
            <h1 id="homeTitle">
               <c:out value="${ dtosize }" /> Computers found
            </h1>
            <div id="actions" class="form-horizontal">
                <div class="pull-left">
                    <form id="searchForm" action="#" method="GET" class="form-inline">

                        <input type="search" id="searchbox" name="search" class="form-control" placeholder="Search name" value="${ search }" />
                        <input type="submit" id="searchsubmit" value="Filter by name" class="btn btn-primary" />
                        <input type="hidden" name="length" value="${ length }">
                    </form>
                </div>
                <div class="pull-right">
                    <a class="btn btn-success" id="addComputer" href="AddComputer">Add Computer</a> 
                    <a class="btn btn-default" id="editComputer" href="#" onclick="$.fn.toggleEditMode();">Edit</a>
                </div>
            </div>
        </div>

        <form id="deleteForm" action="#" method="POST">
            <input type="hidden" name="selection" value="">
            <input type="hidden" name="order" value="${ order }">
            <input type="hidden" name="ascendent" value="${ ascendent }">
        </form>
        

        <form id="pageForm" action="#" method="GET">
            <input type="hidden" name="page" value="">
            <input type="hidden" name="length" value="${ length }">
            <input type="hidden" name="search" value="${ search }">
            <input type="hidden" id="orderParameter" name="order" value="${ order }">
            <input type="hidden" id="ascendentParameter" name="ascendent" value="${ ascendent }">
        </form>
        
        <form id="orderForm" action="#" method="GET">
            <input type="hidden" name="length" value="${ length }">
            <input type="hidden" name="search" value="${ search }">
            <input type="hidden" name="order" value="">
            <input type="hidden" name="ascendent" value="">
        </form>

        <div class="container" style="margin-top: 10px;">
            <table class="table table-striped table-bordered">
                <thead>
                    <tr>
                        <!-- Variable declarations for passing labels as parameters -->
                        <!-- Table header for Computer Name -->

                        <th class="editMode" style="width: 60px; height: 22px;">
                            <input type="checkbox" id="selectall" /> 
                            <span style="vertical-align: top;">
                                 -  <button id="deleteSelected" onclick="$.fn.deleteSelected();">
                                        <i class="fa fa-trash-o fa-lg"></i>
                                    </button>
                            </span>
                        </th>
                        <th><a href="#" id="nameColumn" class="column">
                            Computer name
                        </a></th>
                        <th><a href="#" id="introColumn" class="column">
                            Introduced date
                        </a></th>
                        <!-- Table header for Discontinued Date -->
                        <th><a href="#" id="discontColumn" class="column">
                            Discontinued date
                        </a></th>
                        <!-- Table header for Company -->
                        <th><a href="#" id="companyColumn" class="column">
                            Company
                        </a></th>

                    </tr>
                </thead>
                <!-- Browse attribute computers -->
                <tbody id="results">
                    <c:forEach var="i" items="${ dtolist }">
                        <tr>
	                        <td class="editMode">
	                            <input type="checkbox" name="cb" class="cb" value="${ i.id }">
	                        </td>
	                        <td>
	                           <form id="editForm${ i.id }" action="EditComputer" method="POST">
	                               <input type="hidden" name="id" value="${ i.id }">
	                               <a href="#" onclick="$(&quot;#editForm${ i.id }&quot;).submit()"><c:out value="${ i.nom }"></c:out></a>
	                           </form>
	                        </td>
	                        <td><c:out value="${ i.dateIntroduction }"></c:out></td>
	                        <td><c:out value="${ i.dateDiscontinuation }"></c:out></td>
	                        <td><c:out value="${ i.entreprise.nom }"></c:out></td>
	
	                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </section>

    <footer class="navbar-fixed-bottom">
        <div class="container text-center">
            <ul class="pagination">
                <c:if test="${ page != firstPageNum }">
                    <li>
	                    <a href="#" aria-label="Previous" onclick="$.fn.goToPage(${ 1 });">
	                      <span aria-hidden="true">&laquo;</span>
	                    </a>
	                </li>
                </c:if>
				<c:forEach begin="${ firstPageNum }" end="${ lastPageNum }" var="i">
				    
				    <li><a href="#" <c:if test="${ page == i }"><c:out value="class=currentPage"></c:out></c:if>
				            onclick="$.fn.goToPage(${ i });">
				        <c:out value="${ i }"></c:out>
				    </a></li>
				</c:forEach>
				<c:if test="${ page != lastPageNum }">
					<li>
		                <a href="#" aria-label="Next" onclick="$.fn.goToPage(${ Math.floor((dtosize - 1) / length) + 1 });">
		                    <span aria-hidden="true">&raquo;</span>
		                </a>
		            </li>
                </c:if>
	        </ul>
	        <div class="btn-group btn-group-sm pull-right" role="group" >
				<form action="#" method="get">
					<button type="submit" class="btn btn-default" value="10" name="length">10</button>
					<button type="submit" class="btn btn-default" value="50" name="length">50</button>
					<button type="submit" class="btn btn-default" value="100" name="length">100</button>
					<input type="hidden" name="search" value="${ search }">
		            <input type="hidden" name="order" value="${ order }">
		            <input type="hidden" name="ascendent" value="${ ascendent }">
				</form>
	        </div>
        </div>
    </footer>
</body>
</html>