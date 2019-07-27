<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<link
	href="https://fonts.googleapis.com/css?family=Josefin+Slab|Markazi+Text|Parisienne|Petit+Formal+Script|Pompiere|Rochester&display=swap"
	rel="stylesheet">
<link
	href="https://fonts.googleapis.com/css?family=Cormorant+Infant|Josefin+Slab|Markazi+Text|Parisienne|Petit+Formal+Script|Pompiere|Rochester&display=swap"
	rel="stylesheet">


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login Page</title>
<style>
.wrapper {
	width: 700px;
	margin: 40px;
	margin: 10%;
	text-align: center;
	position: relative;
	display: inline-block;
	font-family: 'Cormorant Infant', serif;
	border: solid gray 1px;
	padding: 20px 5px 200px 5px;
	background-color: white;
	border: none;
	border-radius: 100%
}

.leftBox {
	width: 48%;
	position: relative;
	display: inline-block;
}

.rightBox {
	width: 48%;
	position: relative;
	display: inline-block;
	vertical-align: top;
}

body {
	background-image:
		url("https://images.unsplash.com/photo-1519751138087-5bf79df62d5b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=800&q=60");
	background-repeat: no-repeat;
	background-size: 100% 100%;
	/* opacity: 0.2; */
}

.button {
	font-size: 20px;
	font-family: 'Cormorant Infant', serif;
}
</style>
</head>
<body>
	<div class="wrapper">
		<nav class="navbar navbar-expand-lg navbar-light bg-light">
			<img class="img" style="width: 120px; height: 80px"
				src="../img/mylogo.png">
			<h2 style="font-family: 'Petit Formal Script'">Lele Rose</h2>

			<h1 style="text-align: center;">Welcome</h1>
			<div class="leftBox">
				<h1>Register!</h1>

				<p class="alert-danger">
					<form:errors path="user.*" />
					<c:out value="${emailError}" />
				</p>

				<form:form method="POST" action="/registration" modelAttribute="user">
					<p>
						<form:label path="firstName">First Name:</form:label>
						<form:input type="text" path="firstName" />
					</p>
					<p>
						<form:label path="lastName">Last Name:</form:label>
						<form:input type="text" path="lastName" />
					</p>
					<p>
						<form:label path="email">Email:</form:label>
						<form:input type="email" path="email" />
					</p>

					<p>
						<form:label path="password">Password:</form:label>
						<form:password path="password" />
						<form:errors path="password" />
					</p>
					<p>
						<form:label path="passwordConfirmation">Password Confirmation:</form:label>
						<form:password path="passwordConfirmation" />
					</p>
					<input class="button" type="submit" value="Register!"
						style="background-color: #E5CFCF" />
				</form:form>
			</div>


			<div class="rightBox">
				<h1>Login</h1>
				<p class="alert-danger input-group">
					<c:out value="${logoutMessage}" />
					<c:out value="${loginError}" />
				</p>
				<form method="POST" action="/login">
						<div class="form-group input-group">
						<input name="email" type="text" class="form-control"
							placeholder="Your Email *" />
					</div>
					<div class="form-group input-group">
						<input name="password" type="password" class="form-control"
							placeholder="Your Password *" />
					</div>
					<div class="form-group input-group">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
						<button type="submit" class="btn btn-success btn-block">
							Create Account</button>
						<!-- <a href="/registration">I don't have an account</a> -->
					</div>
			</form>
	</div>
</body>
</html>