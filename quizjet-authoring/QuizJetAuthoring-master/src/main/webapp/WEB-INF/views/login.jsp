<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>Quizjet - Sign In</title>
	<!-- Global Stylesheet -->
	<link rel="stylesheet" href="${baseURL}/resources/css/global.css">
	<!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ==" crossorigin="anonymous">
    <!-- Optional theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" integrity="sha384-aUGj/X2zp5rLCbBxumKTCw2Z50WgIr1vs/PFN4praOTvYXWlVyh2UtNUU0KAUhAX" crossorigin="anonymous">
    <!-- project theme -->
    <link rel="stylesheet" type="text/css" href="${baseURL}/resources/css/global.css">

    <!-- include jQuery -->
    <script type="text/javascript" src="//code.jquery.com/jquery-1.11.3.min.js"></script>
</head>
<body>

	<form id="loginf" name='loginForm' class="form-signin" action="<c:url value='/j_spring_security_check'/>" method='POST'>
		<h2>Quizjet Authoring</h2>
		<c:if test="${not empty error}">
			<div class="alert alert-warning" role="alert">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="alert alert-info" role="alert">${msg}</div>
		</c:if>
		<label for="username" class="sr-only">Username</label>
		<input type="text" class="form-control" id="username" name='username' placeholder="Username" required autofocus/>
		<label for="pwd" class="sr-only">Password</label>
		<input id="pwd" type="password" class="form-control" name='password' placeholder="Password" required />
		<br/>
		<button id="loginb" type="submit" class="btn btn-la btn-primary btn-block">Sign in</button>
	</form>

</body>
</html>