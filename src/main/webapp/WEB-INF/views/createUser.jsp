<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<jsp:include page="partials/header.jsp" />

<div class="container">
	<div class="page-header">
		<h1>
			Creacion de usuario<small>Rol de alumno</small>
		</h1>
	</div>

	<form:form action="create" modelAttribute="user">
		<div class="form-group">
			<label for="userNameInput">Nombre de usuario</label>
			<form:input path="username" placeholder="username" required="required"
				id="userNameInput" />
			<form:errors path="username" cssClass="bg-danger" />
		</div>

		<div class="form-group">
			<label for="emailInput">Email</label>
			<form:input path="email" placeholder="email@example.com" required="required"
				id="emailInput" />
			<form:errors path="email" cssClass="bg-danger" />
		</div>

		<div class="form-group">
			<label for="passwordInput">Contraseña</label>
			<form:input type="password" path="password" placeholder="password"
				required="required" id="passwordInput" />
			<form:errors path="password" cssClass="bg-danger" />
		</div>

		<div class="form-group">
			<label for="birthDateInput">Fecha de nacimiento</label>
			<form:input path="birthDate" placeholder="dd/mm/yyyy"
				required="required" id="birthDateInput" />
			<form:errors path="birthDate" cssClass="bg-danger" />
		</div>

		<div class="form-group">
			<input type="submit" class="btn btn-info" value="Crear pista">
		</div>

	</form:form>

</div>
<jsp:include page="partials/footer.jsp" />