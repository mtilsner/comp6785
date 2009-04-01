<%@ page contentType="text/html;charset=UTF-8" %>

<html>
	<head>
		<meta name="layout" content="main" />
		<title>search results for "${q}"</title>
        <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'search.css')}" />
        <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'clusterOverview.css')}" />
        <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'cluster.css')}" />
        <link rel="stylesheet" href="${createLinkTo(dir:'css/pwc',file:'default.css')}" />
        <link rel="stylesheet" href="${createLinkTo(dir:'css/pwc',file:'mac_os_x.css')}" />
        <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'progressBar.css')}" />
		<g:javascript library="prototype/window" />
        <g:javascript library="progressBar" />
        <g:javascript library="cubansea" />
	</head>
	<body>
		<div id="search">
			<form method="get">
				<input type="text" id="searchField" name="q" value="${q}" />
				<input type="submit" id="searchButton" value="" />
			</form>
		</div>
		<cs:glower id="content">
			<br />
		  	<g:render template="overviewCluster" collection="${clusters}" var="cluster"/>
		</cs:glower>
	</body>
</html>