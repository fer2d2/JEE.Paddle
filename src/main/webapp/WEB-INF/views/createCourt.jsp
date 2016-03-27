<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<jsp:include page="partials/header.jsp" />

<div class="container">
    <div class="page-header">
        <h1>
            Creacion de pista <small>Dar de alta una pista en el
                sistema</small>
        </h1>
    </div>

    <div class="navigation text-right">
        <a href="<c:url value='/courts/list'/>"
            class="btn btn-default text-right" role="button">Ver
            pistas</a>
    </div>

    <form:form action="create" modelAttribute="court">
        <div class="form-group">
            <label for="courtIdInput">Numero de pista</label>
            <form:input path="courtId" placeholder="Id"
                required="required" id="courtIdInput" />
            <form:errors path="courtId" cssClass="bg-danger" />
        </div>

        <div class="form-group">
            <label for="courtActiveInput">Activa</label>
            <form:select path="active">
                <form:options items="${activeOptions}" />
                <form:errors path="active" cssClass="bg-danger" />
            </form:select>
        </div>

        <div class="form-group">
            <input type="submit" class="btn btn-info"
                value="Crear pista">
        </div>
    </form:form>

</div>
<jsp:include page="partials/footer.jsp" />