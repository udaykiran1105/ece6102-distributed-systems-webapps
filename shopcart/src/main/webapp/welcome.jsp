<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.example.shopcart.Genre" %>
<%@ page import="com.example.shopcart.Book" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.lang.String" %>

<html>

<head>
 <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>

<h1 align="center"> Uday Ravuri's Book Shopping App </h1>
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
<%

int first_flag = 0;
UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();
		 if (user != null) {
	        pageContext.setAttribute("user", user);
%>
            <p>Hello, ${fn:escapeXml(user.email)}!</p>
<%
 } else {
Cookie[] allCookies = request.getCookies();
if (allCookies != null) {
for (int i=0; i < allCookies.length; i++) {
if( allCookies[i].getName().equals("email")) {
 first_flag = 1;
}
}
} 
if (first_flag == 0) {
 %>
	    <p>Hello! You need to 
	    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
	    to save your shopping cart.</p>
<%
}
}
String email = null;
if(user != null)
	email = user.getEmail();
 
int flag = 0;

// email = request.getParameter("email");
if (email!=null && !email.isEmpty()){
pageContext.setAttribute("email", email);
Cookie c = new Cookie("email", email);
response.addCookie(c);
flag = 1;
}

Cookie[] allCookies = request.getCookies();
if (allCookies != null) {
for (int i=0; i < allCookies.length; i++) {
if( allCookies[i].getName().equals("email")) { 
email = allCookies[i].getValue();
pageContext.setAttribute("email", email);
flag = 1;
}
}
}
// if (flag == 1) {
%>
<!-- <p>Welcome ${fn:escapeXml(email)}</p> -->
<%
// } else {
%>
<!-- <p>This website uses cookies for shopping cart</p> -->
<!-- <p>Please enter your email address (ID) for your shopping cart</p><br/> -->
<!-- <form action="/welcome.jsp"> -->
<!-- <input type="text" name="email" /> -->
<!-- <button type="submit">Submit</button> -->
<!-- </form> -->
<%

// }

%>

<ul>
<li><a href="/display.jsp?genre=non-fiction">Browse non-fiction books</a></li>
<li><a href="/display.jsp?genre=mystery">Browse mystery books</a></li>
<li><a href="/display.jsp?genre=fiction">Browse fiction books</a></li>
<li><a href="/display.jsp?genre=autobiography">Browse autobiography books</a></li><br/>
<li><a href="/search.jsp">Search for books by author</a></li><br/>
<li><a href="/enter.jsp">Enter new book info</a></li><br/><br/>
<li><a href="/shopcart.jsp">View your cart</a></li>
</ul>
<hr>
<p>Click the button below to erase the entire repository</p>
<button type="submit" onclick="return confirmfunc();">Refresh repository</button>
</body>

</html>
