<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<jsp:include page="partials/header.jsp" />

<div class="container">
    <div class="page-header">
        <h1>
            Listado de Usuarios <small>Listado completo de
                usuarios del sistema</small>
        </h1>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-12">

                <div class="navigation text-right">
                    <a href="<c:url value='/users/create'/>"
                        class="btn btn-default text-right" role="button">Crear
                        usuario</a>
                </div>

                <table class="table table-stripped custom-table">
                    <thead>
                        <tr>
                            <th>Nombre de usuario</th>
                            <th>Email</th>
                            <th>Fecha de nacimiento</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${users}" var="user">
                            <tr>
                                <td>${user.username}</td>
                                <td>${user.email}</td>
                                <td>${user.birthDate.time}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<jsp:include page="partials/footer.jsp" />