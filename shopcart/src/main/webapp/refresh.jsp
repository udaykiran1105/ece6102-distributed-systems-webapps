<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.example.shopcart.Book" %>
<%@ page import="com.example.shopcart.Genre" %>
<%@ page import="com.example.shopcart.OfyHelper" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
 <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
<center><h1>Refresh Page</h1></center>
</head>
<body>
<hr>

<% 
List<Book> books = ObjectifyService.ofy()
			.load()
			.type(Book.class)
			.list();



ObjectifyService.ofy().delete().entities(books).now();

%>

<center><b>Your database is now empty!</b></center>

<a href="welcome.jsp">Home</a>
</body>
