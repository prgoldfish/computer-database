<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

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
</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="ListComputers"> Application - Computer Database </a>
        </div>
    </header>

    <section id="main">
        <div class="container">
        <c:if test="${ addedcomputer == true }">
		   <div class="alert alert-danger">
		      The computer has succesfully been added in the database <br/>
		  </div>
		</c:if>
            <h1 id="homeTitle">
               <c:out value="${ dtosize }" /> Computers found
            </h1>
            <div id="actions" class="form-horizontal">
                <div class="pull-left">
                    <form id="searchForm" action="#" method="GET" class="form-inline">

                        <input type="search" id="searchbox" name="search" class="form-control" placeholder="Search name" />
                        <input type="submit" id="searchsubmit" value="Filter by name" class="btn btn-primary" />
                        <input type="hidden" name="length" value="<c:out value="${ length }"></c:out>">
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
        </form>
        

        <form id="pageForm" action="#" method="GET">
            <input type="hidden" name="page" value="">
            <input type="hidden" name="length" value="<c:out value="${ length }"></c:out>">
            <input type="hidden" name="search" value="<c:out value="${ search }"></c:out>">
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
                                 -  <a href="#" id="deleteSelected" onclick="$.fn.deleteSelected();">
                                        <i class="fa fa-trash-o fa-lg"></i>
                                    </a>
                            </span>
                        </th>
                        <th>
                            Computer name
                        </th>
                        <th>
                            Introduced date
                        </th>
                        <!-- Table header for Discontinued Date -->
                        <th>
                            Discontinued date
                        </th>
                        <!-- Table header for Company -->
                        <th>
                            Company
                        </th>

                    </tr>
                </thead>
                <!-- Browse attribute computers -->
                <tbody id="results">
                    <c:forEach var="i" items="${ dtolist }">
                        <tr>
	                        <td class="editMode">
	                            <input type="checkbox" name="cb" class="cb" value="0">
	                        </td>
	                        <td>
	                            <a href="EditComputer" onclick=""><c:out value="${ i.nom }"></c:out></a>
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
	                    <a href="#" aria-label="Previous" onclick="<c:out value="$.fn.goToPage(${ 1 });"></c:out>">
	                      <span aria-hidden="true">&laquo;</span>
	                    </a>
	                </li>
                </c:if>
				<c:forEach begin="${ firstPageNum }" end="${ lastPageNum }" var="i">
				    
				    <li><a href="#" <c:if test="${ page == i }"><c:out value="class=currentPage"></c:out></c:if>
				            onclick="<c:out value="$.fn.goToPage(${ i });"></c:out>">
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
					<input type="hidden" name="search" value="<c:out value="${ search }"></c:out>">
				</form>
	        </div>
        </div>
    </footer>
<script src="./js/jquery.min.js"></script>
<script src="./js/bootstrap.min.js"></script>
<script src="./js/dashboard.js"></script>

</body>
</html>