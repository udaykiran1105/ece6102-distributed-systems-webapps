<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.example.bookrepo.Book" %>
<%@ page import="com.example.bookrepo.Genre" %>
<%@ page import="com.example.bookrepo.OfyHelper" %>

<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.lang.String" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
    <script>
    function conditionCheck() {
    var x=document.forms["entry_form"]["author"].value;
    var y=document.forms["entry_form"]["title"].value;
    var z=document.forms["entry_form"]["genre"].value;
 
 if (x=="" || x==null || y=="" || y==null || z=="" || z==null) {
    if(x=="" || x==null) 
    msg = "Author name should not be blank\n";
    if(y=="" || y==null) 
    msg+="Title should not be blank\n";
    if(z=="" || z==null)
    msg+="Genre should not be blank";
    
    alert(msg);
    return false;
    
    }
    }
    </script>
    </head>

    <body>

    <%
    Book book;
    int flag=0;
    String genre = request.getParameter("genre");
    String author = request.getParameter("author");
    String title = request.getParameter("title");
    if (genre == null) {
    genre = "unspecified";
    }
    genre = genre.toLowerCase();

    if (author!=null && !author.isEmpty()) {
	if (genre.equals("non-fiction") == true || genre.equals("fiction") == true || genre.equals("mystery") == true || genre.equals("autobiography") == true) {
 pageContext.setAttribute("genre", genre);
         pageContext.setAttribute("author",author);
	         pageContext.setAttribute("title",title);
    // Create the ancestor key
    Key<Genre> theGenre = Key.create(Genre.class, genre);
    List<Book> books = ObjectifyService.ofy()
    			.load()
			.type(Book.class)
			.ancestor(theGenre)
			.order("-date")
			.list();

for (Book itr : books) {
if (title.equalsIgnoreCase(itr.title) == true)
flag++;
}

if (flag == 0){ 
book = new Book(genre, author, title);
ObjectifyService.ofy().save().entity(book).now();
%>
<p align="center">Your book has been saved in the database!</p>
<%
} else {
%>
<p align="center">This book is already in the database!</p>
<%
}

} else {
%>
<p align="center">Please choose a genre among non-fiction, fiction, mystery and autobiography</p>
<%
}

}

%>
    <center>
    <h2>Enter New Book Information</h2>
    <hr>
    <form action="/enter.jsp" name="entry_form" id="titletext" onsubmit="return conditionCheck()" method="get">
    <div>Author's Name:&nbsp<input type="text" name="author"/></div><br/>
    <div>Title:&nbsp<textarea name="title" form="titletext" rows="5" cols="50" placeholder="Enter book title"></textarea></div><br/>
    <div>Genre:&nbsp<input type="text" name="genre" value="${fn:escapeXml(genre)}"/>&nbsp
    </div><br/>
     <div><input type="submit" value="Enter book info"/></div>
    </form>
    </center>
    <hr>
    <a href="welcome.jsp">Home</a>
    </body>
    </html>
