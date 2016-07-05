package com.example.bookrepo;
import java.io.IOException;
import java.util.Properties;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Date;
import java.io.PrintWriter;
import java.lang.String;
public class BookrepoServlet extends HttpServlet {
  @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws IOException {
          resp.setContentType("text/html");
  PrintWriter out = resp.getWriter();
  	 String genre = req.getParameter("genre");
	 out.println("<html>");
	 if (genre == null || genre.isEmpty()) {
	out.println("<body><p>Please click on one of the browse links to view the books</p></body>");
	out.println("</html>");
	out.close();
	 }
	 else {
	 
	  out.println("<head><center><h1>Current titles in " + genre + "</h1></center>");
    out.println("<link type='text/css' rel='stylesheet' href='stylesheets/main.css'>");
    out.println("</head>");
          out.println("<hr>");

out.println("<body bgcolor=\"white\">");

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
out.println("<p>No books have been added to this genre yet</p>");
}
out.println("<ul>");
for (Book book : books) {
        out.println("<li><b>" + book.title + "</b>&nbsp;by&nbsp;<b>" + book.author + "</b></li>"); 
    }

out.println("</ul>");
out.println("<hr>");
out.println("<a href=\"/welcome.jsp\">Home</a>");
out.println("</body></html>");
out.close();
}
}
}
