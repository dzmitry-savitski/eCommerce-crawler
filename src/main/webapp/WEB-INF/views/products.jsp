<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Products</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <div class="panel panel-default">
        <div class="panel-body">
            <a href="/">Back to main page</a>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">Products</div>
        <div class="panel-body">
            <table class="table table-condensed table-striped">
                <thead>
                <tr>
                    <th>id</th>
                    <th>UPC</th>
                    <th>name</th>
                    <th>price</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${products}" var="map">
                    <tr>
                        <td>${map.get("id")}</td>
                        <td>${map.get("upc")}</td>
                        <td>${map.get("name")}</td>
                        <td>${map.get("price")}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </div>
    </div>
</div>
</body>
</html>
