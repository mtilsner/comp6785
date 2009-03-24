<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>welcome to Cuban Sea</title>
  </head>
  <body>
  	<g:form name="search" url="[action:'index']" method="GET">
  		<g:textField name="q" value="${q}" />
  		<g:submitButton name="search" value="search" />
  	</g:form>
  </body>
</html>
