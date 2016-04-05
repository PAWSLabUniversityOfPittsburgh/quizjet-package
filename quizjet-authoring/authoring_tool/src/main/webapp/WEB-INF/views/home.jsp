<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<c:set var="domain" value="${pageContext.request.serverName}" />
<!DOCTYPE html>
<html>
<head>
    <title>QuizJet</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ==" crossorigin="anonymous">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" integrity="sha384-aUGj/X2zp5rLCbBxumKTCw2Z50WgIr1vs/PFN4praOTvYXWlVyh2UtNUU0KAUhAX" crossorigin="anonymous">
    <!-- project theme -->
	<link rel="stylesheet" type="text/css" href="${baseURL}/resources/css/global.css">

    <!-- include react -->
    <script type="text/javascript" src="https://fb.me/react-with-addons-0.14.3.js"></script>
    <!-- include react-dom -->
    <script type="text/javascript" src="https://fb.me/react-dom-0.14.3.js"></script>
    <!-- include jQuery -->
    <script type="text/javascript" src="//code.jquery.com/jquery-1.11.3.min.js"></script>
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">QuizJet Authoring</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-right">
					<li><a>Welcome! ${username}</a></li>
					<li><a href="${baseURL}/login?logout">Log out</a></li>
				</ul>
			</div>
		</div>
	</nav>
	<div id="app" class="container-fluid"></div>
	
	<c:choose>
		<c:when test="${domain == 'localhost'}">
	<script src="http://localhost:9090/webpack-dev-server.js"></script>
    <script type="text/javascript" src="http://localhost:9090/assets/bundle.js"></script>
    	</c:when>
    	<c:otherwise>
    <script type="text/javascript" src="${baseURL}/resources/js/bundle.min.js"></script>
    	</c:otherwise>
	</c:choose>

    <!-- Latest compiled and minified JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ==" crossorigin="anonymous"></script>
</body>
</html>