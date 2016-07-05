<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.example.shopcart.Book" %>
<%@ page import="com.example.shopcart.Genre" %>
<%@ page import="com.example.shopcart.OfyHelper" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>

<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
<h1 align="center">Your shopping cart</h1>
        </head>

<body>
<hr>
<%
int flag = 0;
float tot_price = 0.0f;
String email=null;
Cookie[] allCookies = request.getCookies();
String genre = request.getParameter("genre");

if (allCookies != null) {
for (int i=0; i < allCookies.length; i++) {
if( allCookies[i].getName().equals("email")) { 
email = allCookies[i].getValue();
pageContext.setAttribute("email", email);

flag = 1;
}
}
}


// checkout code
String amt = request.getParameter("checkout");
if (amt != null && email != null) {
	float amount = Float.parseFloat(amt);
	pageContext.setAttribute("tot_price",amount);		
	Key<Genre> theEmail = Key.create(Genre.class, email);
	  List<Book> books = ObjectifyService.ofy()
	  		.load()
			.type(Book.class)
			.ancestor(theEmail)
			.list();
	
ObjectifyService.ofy().delete().entities(books).now();
ObjectifyService.ofy().delete().key(theEmail);
flag = 0;
Cookie c = null;
if(allCookies!=null){ 
for (int i=0; i < allCookies.length; i++) {
c = allCookies[i];
if( allCookies[i].getName().equals("email")) { 
c.setMaxAge(0);
response.addCookie(c);
}
}
}	
%>
<p>Total amount spent: ${fn:escapeXml(tot_price)}</p>
<p>Thank you for your purchase! Come back again.</p>
<hr>
    <a href="/welcome.jsp">Home</a>
</body></html>
<%
}

String[] cart_add = request.getParameterValues("cart_choices");
if (cart_add != null && email!=null && genre!=null) {
	
	Key<Genre> theGenre = Key.create(Genre.class, genre);
    List<Book> books = ObjectifyService.ofy()
    			.load()
			.type(Book.class)
			.ancestor(theGenre)
			.order("-date")
			.list();

Key<Genre> theEmail = Key.create(Genre.class, email);
ObjectifyService.ofy()
    			.load()
			.type(Book.class)
			.ancestor(theEmail)
			.order("-date")
			.list();
			
for (Book itr : books) {

for (String itr2 : cart_add) {
//if(Arrays.binarySearch(cart_add,itr.title) >= 0)
if (itr2.equalsIgnoreCase(itr.title)==true) {
	Book book = new Book(email, itr.author, itr.title, itr.price);
   ObjectifyService.ofy().save().entity(book).now();
}	
}				
}
}

if (flag == 0 && allCookies == null){
%>
<p>Please go back to the home page and enter your email address to save your cart</p>
<p>Thank you</p>
<hr>
    <a href="/welcome.jsp">Home</a>
	</body>
	</html>
<%	
}

// int temp = 0;
String[] cart_delete = request.getParameterValues("delete_choices");
if(cart_delete!=null && email!=null) {
	
	Key<Genre> theEmail = Key.create(Genre.class, email);
	  List<Book> books = ObjectifyService.ofy()
	  		.load()
			.type(Book.class)
			.ancestor(theEmail)
			.list();

// Arrays.sort(cart_delete);
for (Book book : books) {
// if(Arrays.binarySearch(cart_delete,book.title) >= 0)
for (int itr3=0; itr3 < cart_delete.length; itr3++) {
	if((book.title).equals(cart_delete[itr3]) == true) {
	ObjectifyService.ofy().delete().entity(book).now();
		cart_delete[itr3] = null;
	}
}	
// flag = 0;	
}
}

if (flag == 1 && email!=null) {	
%>
<p>Welcome ${fn:escapeXml(email)}!</p> 
<%
Key<Genre> theEmail = Key.create(Genre.class, email);
	  List<Book> books_cart = ObjectifyService.ofy()
	  		.load()
			.type(Book.class)
			.ancestor(theEmail)
			.order("-date")
			.list();
			
			
if (books_cart.isEmpty()){
%>
	<p>Your cart is empty. Go to the search or browse pages to add books here</p><br/>
	<hr>
    <a href="/welcome.jsp">Home</a>
	</body></html>	
<%	
} else {
	
%>
	<form id="show_form" method="get" action="/shopcart.jsp" >
<%
// float tot_price = 0.0f;
DecimalFormat df = new DecimalFormat("0.00");
df.setMaximumFractionDigits(2);

for (Book book : books_cart) {
	pageContext.setAttribute("title",book.title);
	pageContext.setAttribute("author",book.author);
	pageContext.setAttribute("price",book.price);
	tot_price += Float.parseFloat(df.format(Float.parseFloat(book.price)));
%>
 <input type="checkbox" name="delete_choices" value="${fn:escapeXml(title)}" />
 <b>${fn:escapeXml(title)}</b>&nbsp;by&nbsp;<b>${fn:escapeXml(author)} | Price:&nbsp;</b>${fn:escapeXml(price)}<br/>
<%
}
pageContext.setAttribute("tot_price",tot_price);

%>
	<p>Total Price: ${fn:escapeXml(tot_price)}</p>
	<button type="submit">Delete items</button>&nbsp&nbsp
	
<button type="submit" name="checkout" value="${fn:escapeXml(tot_price)}">Checkout</button><br/>
</form>
	<hr>
<a href="/welcome.jsp">Home</a>


</body>
</html>
<%
}
}
%>
