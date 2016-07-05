package com.example.bookrepo;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.lang.String;
import java.util.Date;
import java.util.List;

/**
 * The @Entity tells Objectify about our entity.  We also register it in {@link OfyHelper}
  * Our primary key @Id is set automatically by the Google Datastore for us.
   *
    * We add a @Parent to tell the object about its ancestor. We are doing this to support many
     * guestbooks.  Objectify, unlike the AppEngine library requires that you specify the fields you
      * want to index using @Index.  Only indexing the fields you need can lead to substantial gains in
       * performance -- though if not indexing your data from the start will require indexing it later.
        *
	 * NOTE - all the properties are PUBLIC so that can keep the code simple.
	  **/
	  @Entity
	  public class Book {
	    @Parent Key<Genre> theGenre;
	      @Id public Long id;

	        public String author;
		  
		    public String title;
		      @Index public Date date;

		        /**
			   * Simple constructor just sets the date
			      **/
			        public Book() {
				    date = new Date();
				      }


public Book(String genre, String author, String title) {
this();
 if(genre != null ) {
     theGenre = Key.create(Genre.class, genre);  // Creating the Ancestor key
 } else {
     theGenre = Key.create(Genre.class, "fiction");
 }
this.title=title;
this.author=author;
}
}	 



