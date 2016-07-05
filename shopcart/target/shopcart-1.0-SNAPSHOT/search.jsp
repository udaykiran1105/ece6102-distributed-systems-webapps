<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.example.shopcart.Genre" %>
<%@ page import="com.example.shopcart.Book" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ page import="java.lang.String" %>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
         <script>
	   function conditionCheck() {
	   var x=document.forms["search_form"]["author"].value;
	   var y=document.forms["search_form"]["genre"].value;
	   if(x == "" || x == null || y=="" || y==null) {
	   if (x=="" || x==null)
	   msg="Author name should not be blank\n";
	   if (y=="" || y==null)
	   msg+="Genre should not be blank";
	   
	   alert(msg);
	   return false;
	   }
	   }
	   </script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js"></script>
	</head>
	    <body>     
	          <center>
                  <h2>Search Page</h2>
                  <hr>

				  
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
        String author = request.getParameter("author");
 String xyz=author;
  pageContext.setAttribute("genre", genre);
%> 
 <script type="text/javascript" >
function myfunction() {   
	$("#search_check").append('<input type="hidden" name="genre" value=${fn:escapeXml(genre)} />');
	$("#search_check").submit();
} 
</script>
<%
    if (genre == null)
    genre="unspecified";   
 
 if (flag == 1) {
%>
	<p>Welcome ${fn:escapeXml(email)}!</p>
<%
}
 
 if (author != null && !author.isEmpty()) { 
     genre = genre.toLowerCase();
     

    // Create the correct Ancestor key
          Key<Genre> theGenre = Key.create(Genre.class, genre);
// Run an ancestor query to ensure we see the most up-to-date
    // view of the books belonging to the selected Genre.
          List<Book> books = ObjectifyService.ofy()
	                       .load()
		                .type(Book.class) // We want only Books
	   	                .ancestor(theGenre)    // Anyone in this genre
		                 .order("-date")
		                 .limit(50)
		                 .list();


if (books.isEmpty() == false) {  

	String temp;

pageContext.setAttribute("author",author);
%>

<form id="search_check" method="get" action="/shopcart.jsp">
<%
for (Book book : books) {
	pageContext.setAttribute("title", book.title);
	pageContext.setAttribute("price", book.price);
temp = book.author;
pageContext.setAttribute("temp", temp);	
	if (xyz != null) {

%>

<c:set var="var1" value="${fn:escapeXml(temp)}"></c:set>
<c:set var="var1l" value="${fn:toLowerCase(var1)}"></c:set>
<c:set var="var2" value="${fn:escapeXml(author)}"></c:set>
<c:set var="var2l" value="${fn:toLowerCase(var2)}"></c:set>

<c:if test="${fn:contains(var1l, var2l)}">
<input type="checkbox" name="cart_choices" value="${fn:escapeXml(title)}" /> 
<b>${fn:escapeXml(title)}</b> by <b>${fn:escapeXml(temp)}</b> | <b>Price: </b>${fn:escapeXml(price)}<br/>
<c:set var="flag" value="1"></c:set>
</c:if>
<%

	} // printing condition

 }
%>
<button type="submit" onclick="myfunction()"/>Add to Cart</button><br/> 
</form> 
<%
}


if (pageContext.getAttribute("flag") != "1" && (genre.equals("autobiography")==true || genre.equals("fiction")==true || genre.equals("non-fiction")==true || genre.equals("mystery")==true)) {
%>
<p> There are no <b>${fn:escapeXml(genre)}</b> books written by <b>${fn:escapeXml(author)}</b> in the database
</p>
<c:set var="flag" value="0"></c:set>
<%
}  // flag check



if (genre.equals("autobiography")==false && genre.equals("fiction")==false && genre.equals("non-fiction")==false && genre.equals("mystery")==false) {

%>
<p>Please choose a genre among non-fiction, mystery, fiction and autobiography</p>
<%

}

}
       %>


                  <form action="/search.jsp" name="search_form" onsubmit="return conditionCheck()" method="get">
                  <div>Author's Name:&nbsp;<input type="text" name="author"/></div><br/>
                  <div>Genre:&nbsp;<input type="text" name="genre"/>&nbsp;</div><br/>
		   <div><input type="submit" name="search" value="Search"/></div>
                  </form>
		 </center>
		 <hr>
		 <a href="welcome.jsp">Home</a>
              </body>
</html>

