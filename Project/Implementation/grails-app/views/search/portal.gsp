<%@ page contentType="text/html;charset=UTF-8" %>

<html>
	<head>
		<meta name="layout" content="main" />
		<title>fine grained search control</title>
        <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'portal.css')}" />
	</head>
	<body>
		<div id="search">
			<form method="get">
				<input type="text" id="searchField" name="q" value="${q}" />
				<input type="submit" id="searchButton" value="" />
			</form>
			<div id="messages"></div>
		</div>
	</body>
</html>
