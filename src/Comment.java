import java.util.ArrayList;

public class Comment{
	
	protected int id;
	protected PolyglotDatabase db;
	
	public Comment(PolyglotDatabase db){
		this.db = db;
	}
	
	public void setID(int id) {
		this.id=id;
		
	}
	
	public int getID() {
		return id;
	}
	
	public Post getPost(){
		return db.getpost(this);
	}
	
	public void setPost(Post element){
		db.set(this, "post", element.getID());
	}
	
	public void addReplies(Comment element){
		element.setComment(this);
	}
	
	public ArrayList<Comment> getReplies(){
		return db.getReplies(this);
	}
	
	public ArrayList<Comment> getAllReplies(){
		ArrayList<Comment> all = (ArrayList<Comment>) getReplies().clone(); 
		for(Comment member :getReplies()) {
			if(member.getReplies().size()!=0) {
				ArrayList<Comment> allchild = member.getAllReplies();
				if(allchild.size()>0)
					all.addAll(allchild);
			}
		}

		return all;
	}
	
	public Comment Findreplies(String title) {
		for(Comment element: getAllReplies()) {
			if(element.getTitle()==title)
				return element;
		}
		return null;
	}
	
	public Comment getReplyto(){
		return db.getreplyto(this);
	}
	
	public void setReplyto(Comment element){
		db.set(this, "replyto", element.getID());
	}
	
	public String getTitle() {
		return db.get(this,"title");
	}
	
	public void setTitle(String title) {
		db.set(this,"title",title);
	}
	
	public String getBody() {
		return db.get(this,"body");
	}
	
	public void setBody(String body) {
		db.set(this,"body",body);
	}
	
}	

