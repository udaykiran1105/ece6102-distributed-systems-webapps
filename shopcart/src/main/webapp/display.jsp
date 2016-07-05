<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.example.shopcart.Book" %>
<%@ page import="com.example.shopcart.Genre" %>
<%@ page import="com.example.shopcart.OfyHelper" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.lang.String" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<%
int flag = 0;
Cookie[] allCookies = request.getCookies();
if (allCookies != null) {
for (int i=0; i < allCookies.length; i++) {
if( allCookies[i].getName().equals("email")) { 
String email = allCookies[i].getValue();
pageContext.setAttribute("email", email);
flag = 1;
}
}
}

String genre = request.getParameter("genre");
if(genre!=null)
pageContext.setAttribute("genre", genre);


%>
<head><center><h1>Current titles in ${fn:escapeXml(genre)} </h1></center>
<link type='text/css' rel='stylesheet' href='stylesheets/main.css'/>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
</head>
<hr>
<body>

<script type="text/javascript" >
function myfunction() {   
	$("#browse_form").append('<input type="hidden" name="genre" value=${fn:escapeXml(genre)} />');
	$("#browse_form").submit();
} 
</script>


<%
if (flag == 1) {
%>
	<p>Welcome ${fn:escapeXml(email)}!</p>
<%
}
	
genre = genre.toLowerCase();
Key<Genre> theGenre = Key.create(Genre.class, genre);
	  List<Book> books = ObjectifyService.ofy()
	  		.load()
			.type(Book.class)
			.ancestor(theGenre)
			.order("-date")
			.limit(50)
			.list();

			if (books.isEmpty()){

%>
<p>No books have been added to this genre yet</p><br/>
<hr>
<a href="/welcome.jsp">Home</a>
</body></html>
<%
} else {
%>
	<br/>
	<form id="browse_form" method="get" action="/shopcart.jsp" >
	
<%
for (Book book : books) {
	pageContext.setAttribute("title",book.title);
	pageContext.setAttribute("author",book.author);
	pageContext.setAttribute("price",book.price);
	
%>
   <input type="checkbox" name="cart_choices" value="${fn:escapeXml(title)}" /> 
   <b>${fn:escapeXml(title)}</b>&nbsp;by&nbsp;<b>${fn:escapeXml(author)} | Price:&nbsp;</b>${fn:escapeXml(price)}<br/>
   
<%  
  }

%>
<button type="submit" onclick="myfunction()"/>Add to Cart</button><br/> 
</form>

<hr>
<a href="/welcome.jsp">Home</a>
</body></html>
<%
}
%>


