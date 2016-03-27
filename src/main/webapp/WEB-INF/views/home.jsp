<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<jsp:include page="partials/header.jsp" />

<div class="container">
    <div class="page-header">
        <h1>
            Padel UPM <small>Sistema de gestion de pistas de
                padel</small>
        </h1>
    </div>

    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="list-group">
                    <a href="<c:url value='/courts/list'/>"
                        class="list-group-item">Ver pistas</a> <a
                        href="<c:url value='/courts/create'/>"
                        class="list-group-item">Crear pistas</a> <a
                        href="<c:url value='/users/list'/>"
                        class="list-group-item">Ver usuarios</a> <a
                        href="<c:url value='/users/create'/>"
                        class="list-group-item">Crear usuarios</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="partials/footer.jsp" />