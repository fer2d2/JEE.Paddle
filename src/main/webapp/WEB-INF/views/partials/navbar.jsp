<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<nav class="navbar navbar-inverse navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">
				
				<span class="icon-bar"></span><span class="icon-bar"></span> <span
					class="icon-bar"></span>
					
			</button>
			<a class="navbar-brand" href="#">Paddle UPM</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li><a href="<c:url value='/home'/>">Inicio</a></li>
				<li><a href="<c:url value='#'/>">Mostrar pistas</a></li>
				<li><a href="<c:url value='#'/>">Alta de pista</a></li>
			</ul>
		</div>
	</div>
</nav>