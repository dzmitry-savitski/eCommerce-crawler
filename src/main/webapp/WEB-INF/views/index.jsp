<%@ taglib prefix="h" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Parser prototype</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script src="<h:url value="/resources/js/main.js"/>"></script>
</head>
<body onload="showProgress(${startProgressBar})">
<div class="container">
    <div class="panel panel-default">
        <div class="panel-heading">Options</div>
        <div class="panel-body">

            <form:form role="form" action="/start" method="post" class="form-inline">
                <div class="form-group">
                    <label for="url">Site url:</label>
                        <%--http://www.biglots.com/--%>
                    <input type="text" class="form-control" id="url" name="url" placeholder="http://www.shop.com/">
                </div>
                <div class="form-group">
                    <label for="aiteType">Site type:</label>
                    <select class="form-control" id="aiteType">
                        <option selected="selected">type 1</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-success btn-lg" onclick="showProgress();" ${startButton}>Start
                </button>
            </form:form>
            <div class="progress">
                <div class="progress-bar" id="progress" role="progressbar" style="width:0%"></div>
            </div>
            <strong>Threads: </strong>
            <p class="text-info">10</p>
            <strong>URL regexp: </strong>
            <p class="text-info"><code>"/product/"</code></p>
            <strong>Product regexp: </strong>
            <p class="text-info">
                <code>&quot;&lt;h1&gt;(?&lt;name&gt;.*?)&lt;/h1&gt;.*SKU&lt;/span&gt;(?&lt;upc&gt;.*?)&lt;/div&gt;.*regular-price&quot;&gt;.*?\$(?&lt;price&gt;.*?)&lt;/div&gt;&quot;</code>
            </p>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-body row">
            <div class="col-sm-4">
                <form:form role="form" action="/logClear" method="post">
                    <button type="submit" class="btn btn-primary">Clear log file</button>
                </form:form>
            </div>
            <div class="col-sm-4">
                <form:form role="form" action="/products" method="get">
                    <button type="submit" class="btn btn-primary">Show all products</button>
                </form:form>
            </div>
            <div class="col-sm-4">
                <form:form role="form" action="/productsClear" method="post">
                    <button type="submit" class="btn btn-primary">Clear products table</button>
                </form:form>
            </div>
        </div>
    </div>

    <div class="panel panel-default">
        <div class="panel-heading">Log</div>
        <div class="panel-body">
            <iframe id="logFrame" src="<h:url value="/log"/> " width="100%" height="400px"
                    onLoad="scrollLog()"></iframe>
        </div>
    </div>

</div>
</body>
</html>
