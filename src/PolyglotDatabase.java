import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class PolyglotDatabase {

	public class Mysql{
		protected  Connection conn;
		
		public Mysql(String url, String username, String password) {
			 try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			 try {
					conn = DriverManager.getConnection(url,username,password);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		public boolean runSQL(String sql) {
			Statement stmt;
			try {
				stmt = conn.createStatement();
				int result;
				result = stmt.executeUpdate(sql);
				if (result != -1) 
					return true;
				else
					return false;
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			return false;
		}
		
		public boolean TableIsExit(String table) {
			Statement stmt;
			try {
				String sql = "show tables like \""+table+"\"";
				stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery(sql);
				if (resultSet.next()) 
					return true;
				else
					return false;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return false;
		}
        
		public ResultSet  findTable(String name) {
			String sql = "SELECT * FROM "+name;
			Statement stmt;
			try {
				stmt = conn.createStatement();
				if(TableIsExit(name))
					return stmt.executeQuery(sql);
				else
					return null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	protected Mysql mysql_test;
	protected MongoDatabase mongo_newtest;

	public PolyglotDatabase(){
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=GMT";
		mysql_test = new Mysql(url,"root","");

		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		mongo_newtest= mongoClient.getDatabase("newtest");
	
		if(!mysql_test.TableIsExit("Post")) 
			mysql_test.runSQL("CREATE TABLE Post(id int,Title varchar(255),Page int,Price double,author int)");
		
		if(!mysql_test.TableIsExit("Author")) 
			mysql_test.runSQL("CREATE TABLE Author(id int,name varchar(255),post int)");
		
		if(!mysql_test.TableIsExit("fans_Author")) 
			mysql_test.runSQL("CREATE TABLE fans_Author(idol int,fans int)");
			
		mongo_newtest.getCollection("Comment");
		
		if(!mysql_test.TableIsExit("Fan")) 
			mysql_test.runSQL("CREATE TABLE Fan(id int,name varchar(255))");
		
		if(!mysql_test.TableIsExit("idol_Fan")) 
			mysql_test.runSQL("CREATE TABLE idol_Fan(fans int,idol int)");
			
	}
	
	public Post createPost() {
		ResultSet rs = mysql_test.findTable("Post");
		int id =0;
		try {
			while(rs.next())
				id=rs.getInt("id");
			id++;
			Post post = new Post(this);
			post.setID(id);
			mysql_test.runSQL("INSERT INTO Post(id) VALUES("+id+")");
			return post;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void set(Post post, String name, Object value) {
		if(value ==null)
			mysql_test.runSQL("UPDATE Post SET "+name+" = null WHERE id = "+post.getID());
		else {
			if(value.getClass().getSimpleName().equals("String"))
				mysql_test.runSQL("UPDATE Post SET "+name+" = '"+value+"' WHERE id = "+post.getID());
			else
				mysql_test.runSQL("UPDATE Post SET "+name+" = "+value+" WHERE id = "+post.getID());
			}
	}
	
	public String get(Post post, String name) {
		ResultSet rs =mysql_test.findTable("Post");
		try {
			while(rs.next())
			{
				if(rs.getInt("id")==post.getID())
					return rs.getString(name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Author createAuthor() {
		ResultSet rs = mysql_test.findTable("Author");
		int id =0;
		try {
			while(rs.next())
				id=rs.getInt("id");
			id++;
			Author author = new Author(this);
			author.setID(id);
			mysql_test.runSQL("INSERT INTO Author(id) VALUES("+id+")");
			return author;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void set(Author author, String name, Object value) {
		if(value ==null)
			mysql_test.runSQL("UPDATE Author SET "+name+" = null WHERE id = "+author.getID());
		else {
			if(value.getClass().getSimpleName().equals("String"))
				mysql_test.runSQL("UPDATE Author SET "+name+" = '"+value+"' WHERE id = "+author.getID());
			else
				mysql_test.runSQL("UPDATE Author SET "+name+" = "+value+" WHERE id = "+author.getID());
			}
	}
	
	public String get(Author author, String name) {
		ResultSet rs =mysql_test.findTable("Author");
		try {
			while(rs.next())
			{
				if(rs.getInt("id")==author.getID())
					return rs.getString(name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Comment createComment() {
		MongoCollection<Document> comments = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = comments.find();
		int id =0;
		for (Document document : documents) 
		   id = document.getInteger("id");
		id++;
		Comment comment = new Comment(this);
		comment.setID(id);
		Document doc = new Document();
	    doc.put("id", id);
	    comments.insertOne(doc);
		return comment;
	}
	
	public void set(Comment comment, String name, Object value) {
		MongoCollection<Document> comments = mongo_newtest.getCollection("Comment");
		comments.updateOne(Filters.eq("id", comment.getID()), new Document("$set",new Document(name,value)));	
	}
	
	public String get(Comment comment, String name) {
		MongoCollection<Document> comments = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = comments.find();
		for (Document document : documents) {
		    if(document.getInteger("id")==comment.getID())
		    	return document.get(name).toString();
		}
		return null;
	}
	
	public Fan createFan() {
		ResultSet rs = mysql_test.findTable("Fan");
		int id =0;
		try {
			while(rs.next())
				id=rs.getInt("id");
			id++;
			Fan fan = new Fan(this);
			fan.setID(id);
			mysql_test.runSQL("INSERT INTO Fan(id) VALUES("+id+")");
			return fan;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void set(Fan fan, String name, Object value) {
		if(value ==null)
			mysql_test.runSQL("UPDATE Fan SET "+name+" = null WHERE id = "+fan.getID());
		else {
			if(value.getClass().getSimpleName().equals("String"))
				mysql_test.runSQL("UPDATE Fan SET "+name+" = '"+value+"' WHERE id = "+fan.getID());
			else
				mysql_test.runSQL("UPDATE Fan SET "+name+" = "+value+" WHERE id = "+fan.getID());
			}
	}
	
	public String get(Fan fan, String name) {
		ResultSet rs =mysql_test.findTable("Fan");
		try {
			while(rs.next())
			{
				if(rs.getInt("id")==fan.getID())
					return rs.getString(name);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setFans(Author author,Fan fan){
		mysql_test.runSQL("INSERT INTO fans_Author(idol ,fans ) VALUES("+author.getID()+","+fan.getID()+")");
	}
		
	public void setIdol(Fan fan,Author author){
		mysql_test.runSQL("INSERT INTO idol_Fan(fans ,idol ) VALUES("+fan.getID()+","+author.getID()+")");
	}
		
	public ArrayList<Comment> getComments(Post element) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = table.find();
		for (Document document : documents) {
		    if(document.getInteger("post")!=null) {
		    	if(document.getInteger("post")==element.getID()) {
		    	Comment comment = new Comment(this);
		    	comment.setID(document.getInteger("id"));
		    	comments.add(comment);
		    	}
		    }
		}
		return comments;
	}
		
	public Author getauthor(Post element) {
		ResultSet rs =mysql_test.findTable("Post");
		try {
			while(rs.next())
			{
				if(rs.getInt("id")==element.getID())
				{
					Author author =new Author(this);
					author.setID(rs.getInt("author"));
					return author;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Post getpost(Author element) {
		ResultSet rs =mysql_test.findTable("Author");
		try {
			while(rs.next())
			{
				if(rs.getInt("id")==element.getID())
				{
					Post post =new Post(this);
					post.setID(rs.getInt("post"));
					return post;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<Fan> getFans(Author element) {
		ResultSet rs =mysql_test.findTable("fans_Author");
		ArrayList<Fan> fans = new ArrayList<Fan>();
		try {
			while(rs.next())
			{
				if(rs.getInt("idol")==element.getID())
				{
					Fan fan =new Fan(this);
					fan.setID(rs.getInt("fans"));
					fans.add(fan);
				}
			}
			return fans;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Post getpost(Comment element) {
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = table.find();
		for (Document document : documents) {
		    if(document.getInteger("id")==element.getID()) {
		    	if(document.getInteger("post")!=null) {
		    	Post  post = new Post(this);
		    	post.setID(document.getInteger("post"));
		    	return post;
		    	}
		    }
		}
		return null;
	}
	
	public ArrayList<Comment> getReplies(Comment element) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = table.find();
		for (Document document : documents) {
		    if(document.getInteger("replyto")!=null) {
		    	if(document.getInteger("replyto")==element.getID()) {
		    	Comment comment = new Comment(this);
		    	comment.setID(document.getInteger("id"));
		    	comments.add(comment);
		    	}
		    }
		}
		return comments;
	}
		
	public Comment getreplyto(Comment element) {
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = table.find();
		for (Document document : documents) {
		    if(document.getInteger("id")==element.getID()) {
		    	if(document.getInteger("replyto")!=null) {
		    	Comment  comment = new Comment(this);
		    	comment.setID(document.getInteger("replyto"));
		    	return comment;
		    	}
		    }
		}
		return null;
	}
	
	public ArrayList<Author> getIdol(Fan element) {
		ResultSet rs =mysql_test.findTable("idol_Fan");
		ArrayList<Author> authors = new ArrayList<Author>();
		try {
			while(rs.next())
			{
				if(rs.getInt("fans")==element.getID())
				{
					Author author =new Author(this);
					author.setID(rs.getInt("idol"));
					authors.add(author);
				}
			}
			return authors;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Post findPostByTitle(String title){
		ResultSet rs =mysql_test.findTable("Post");
		try {
			while(rs.next())
			{	
				if(rs.getString("title").equals(title))
	
				{
					Post post =new Post(this);
					post.setID(rs.getInt("id"));
					return post;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Author findAuthorByName(String name){
		ResultSet rs =mysql_test.findTable("Author");
		try {
			while(rs.next())
			{	
				if(rs.getString("name").equals(name))
	
				{
					Author author =new Author(this);
					author.setID(rs.getInt("id"));
					return author;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Comment findCommentByTitle(String title){
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		FindIterable<Document> documents = table.find();
		for (Document document : documents) {
		if(document.get("title").equals(title)) {
		    	Comment comment =new Comment(this);
		    	comment.setID(document.getInteger("id"));
		    	return comment;
		    }
		}
		return null;
	}
	
	public Fan findFanByName(String name){
		ResultSet rs =mysql_test.findTable("Fan");
		try {
			while(rs.next())
			{	
				if(rs.getString("name").equals(name))
	
				{
					Fan fan =new Fan(this);
					fan.setID(rs.getInt("id"));
					return fan;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void deletePost(Post post) {
		for(Comment element : post.getComments()){
			set(element, "Post", null);
		}
		set(post.getAuthor(), "Post", null);
		mysql_test.runSQL("DELETE FROM Post WHERE id ="+ post.getID());
	}
	
	public void deleteAuthor(Author author) {
		set(author.getPost(), "Author", null);
		mysql_test.runSQL("DELETE FROM fans_Author WHERE idol ="+ author.getID());
		mysql_test.runSQL("DELETE FROM idol_Fan WHERE idol ="+ author.getID());
		mysql_test.runSQL("DELETE FROM Author WHERE id ="+ author.getID());
	}
	
	public void deleteComment(Comment comment) {
		for(Comment element : comment.getReplies()){
			set(element, "Comment", null);
		}
		MongoCollection<Document> table = mongo_newtest.getCollection("Comment");
		table.deleteOne(Filters.eq("id", comment.getID()));
	}
	
	public void deleteFan(Fan fan) {
		mysql_test.runSQL("DELETE FROM idol_Fan WHERE fans ="+ fan.getID());
		mysql_test.runSQL("DELETE FROM fans_Author WHERE fans ="+ fan.getID());
		mysql_test.runSQL("DELETE FROM Fan WHERE id ="+ fan.getID());
	}
	
	public void deleteFans(Author author, Fan fan) {
		mysql_test.runSQL("DELETE FROM fans WHERE Author ="+ author.getID() +" and Fan ="+ fan.getID());			
	}
	
	public void deleteIdol(Fan fan, Author author) {
		mysql_test.runSQL("DELETE FROM idol WHERE Fan ="+ fan.getID() +" and Author ="+ author.getID());			
	}
	
}

				
