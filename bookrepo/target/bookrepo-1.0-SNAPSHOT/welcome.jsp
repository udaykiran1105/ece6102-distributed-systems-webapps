<%@ page import="java.util.List" %>
<%@ page import="com.example.bookrepo.Genre" %>
<%@ page import="com.example.bookrepo.Book" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
 <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>

<h1 align="center"> Uday Ravuri's Book Repository </h1>
<script type="text/javascript">
function confirmfunc() {
var x = confirm("Are you sure? All the books in the repository will be erased!!");
if (x == true) 
{  window.open("/refresh.jsp");
   return true;
}
else 
return false;
}
</script>
</head>
<body color="#CFCCC9">
<ul>
<li><a href="/display?genre=non-fiction">Browse non-fiction books</a></li>
<li><a href="/display?genre=mystery">Browse mystery books</a></li>
<li><a href="/display?genre=fiction">Browse fiction books</a></li>
<li><a href="/display?genre=autobiography">Browse autobiography books</a></li><br/>
<li><a href="/search.jsp">Search for books by author</a></li><br/>
<li><a href="/enter.jsp">Enter new book info</a></li><br/>
</ul>
<p>Click the button below to erase the entire repository</p>
<button type="submit" onclick="return confirmfunc();">Refresh repository</button>
</body>

</html>
