import java.util.ArrayList;

public class Post{
	
	protected int id;
	protected PolyglotDatabase db;
	
	public Post(PolyglotDatabase db){
		this.db = db;
	}
	
	public void setID(int id) {
		this.id=id;
		
	}
	
	public int getID() {
		return id;
	}
	
	public void addComments(Comment element){
		element.setPost(this);
	}
	
	public ArrayList<Comment> getComments(){
		return db.getComments(this);
	}
	
	public Comment Findcomments(String title) {
		for(Comment element: getComments()) {
			if(element.getTitle()==title)
				return element;
		}
		return null;
	}
	
	public Author getAuthor(){
		return db.getauthor(this);
	}
	
	public void setAuthor(Author element){
		if(this.getAuthor()==null) {
			db.set(this, "author", element.getID());
			element.setPost(this);
		}
		else{
			if(this.getAuthor().getID()!=element.getID()) {
				db.set(this.getAuthor(), "Post", null);
				db.set(this, "author", element.getID());
				element.setPost(this);
			}
		}
	}
	
	public String getTitle() {
		return db.get(this,"Title");
	}
	
	public void setTitle(String Title) {
		db.set(this,"Title",Title);
	}
	
	public int getPage() {
		return Integer.parseInt(db.get(this,"Page"));
	}
	
	public void setPage(int Page) {
		db.set(this,"Page",Page);
	}
	
	public double getPrice() {
		return Double.valueOf(db.get(this,"Price"));
	}
	
	public void setPrice(double Price) {
		db.set(this,"Price",Price);
	}
	
}	

