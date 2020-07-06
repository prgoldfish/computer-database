<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
	<title>Computer Database</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!-- Bootstrap -->
    <link href=<spring:url value="/css/bootstrap.min.css"/> rel="stylesheet" media="screen">
    <link href=<spring:url value="/css/font-awesome.css"/> rel="stylesheet" media="screen">
    <link href=<spring:url value="/css/main.css"/> rel="stylesheet" media="screen">
</head> 
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href=<spring:url value="/ListComputers"/>> <spring:message code="header.name"/></a>
        </div>
    </header>

	<section id="main">
		<div class="container">
			<div class="alert alert-danger">
			     <spring:message code="403.message"/>
				<br/>
				<!-- stacktrace -->
			</div>
		</div>
	</section>

    <script src=<spring:url value="/js/jquery.min.js"/>></script>
    <script src=<spring:url value="/js/bootstrap.min.js"/>></script>
    <script src=<spring:url value="/js/dashboard.js"/>></script>
</body>
</html>