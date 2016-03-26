<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<nav class="navbar navbar-inverse navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#navbar" aria-expanded="false"
				aria-controls="navbar">

				<span class="icon-bar"></span><span class="icon-bar"></span> <span
					class="icon-bar"></span>

			</button>
			<a class="navbar-brand" href="<c:url value='/home'/>">Paddle UPM</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li><a href="<c:url value='/home'/>">Inicio</a></li>
				<li><a href="<c:url value='/courts/list'/>">Gestion de pistas</a></li>
				<li><a href="<c:url value='/users/list'/>">Gestion de usuarios</a></li>
			</ul>
		</div>
	</div>
</nav>