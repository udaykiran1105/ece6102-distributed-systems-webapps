package com.example.shopcart;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

   public class OfyHelper implements ServletContextListener {
     public void contextInitialized(ServletContextEvent event) {
         // This will be invoked as part of a warmup request, or the first user request if no warmup
	     // request.
	         ObjectifyService.register(Genre.class);
		     ObjectifyService.register(Book.class);
			 
		       }

		         public void contextDestroyed(ServletContextEvent event) {
			     // App Engine does not currently invoke this method.
			       }
			       }
