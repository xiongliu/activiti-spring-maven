<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>Persons</h1>

<table style="border: 1px solid; width: 500px; text-align:center">
	<thead style="background:#fcf">
		<tr>
			<th>First Name</th>
			<th>Last Name</th>
			<th>Money</th>
			<th colspan="3"></th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${persons}" var="person">
			<c:url var="editUrl" value="/persons/edit?id=${person.id}" />
			<c:url var="deleteUrl" value="/persons/delete?id=${person.id}" />
			<c:url var="addUrl" value="/persons/add" />
		<tr>
			<td><c:out value="${person.firstName}" /></td>
			<td><c:out value="${person.age}" /></td>
			<td><a href="${editUrl}">Edit</a></td>
			<td><a href="${deleteUrl}">Delete</a></td>
			<td><a href="${addUrl}">Add</a></td>
		</tr>
	</c:forEach>
	</tbody>
</table>

</body>
</html>