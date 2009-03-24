<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>search results for "${q}"</title>
  </head>
  <body>
    <g:render template="cluster" collection="${clusters}" var="cluster"/>
  </body>
</html>
