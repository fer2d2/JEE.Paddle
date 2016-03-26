<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<jsp:include page="partials/header.jsp" />

<div class="container">
	<div class="page-header">
		<h1>
			Listado de pistas <small>pistas disponibles y no disponibles</small>
		</h1>
	</div>

	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<table class="table table-stripped custom-table">
					<thead>
						<tr>
							<th>Número de pista</th>
							<th>Activa</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${courts}" var="court">
							<tr>
								<td>${court.courtId}</td>
								<td><c:if test="${court.active == true}">
                                        Si
                                    </c:if> <c:if
										test="${court.active == false}">
                                        No
                                    </c:if></td>
								<td><a
									href="<c:url value='/courts/set-active/${court.courtId}' />"><i
										class="glyphicon glyphicon-ok"></i></a> <a
									href="<c:url value='/courts/set-inactive/${court.courtId}' />"><i
										class="glyphicon glyphicon-remove"></i></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>

<jsp:include page="partials/footer.jsp" />