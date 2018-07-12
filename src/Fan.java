import java.util.ArrayList;

public class Fan{
	
	protected int id;
	protected PolyglotDatabase db;
	
	public Fan(PolyglotDatabase db){
		this.db = db;
	}
	
	public void setID(int id) {
		this.id=id;
		
	}
	
	public int getID() {
		return id;
	}
	
	public void addIdol(Author element){
		db.setIdol(this , element);
		db.setFans(element, this);
	}
	
	public ArrayList<Author> getIdol(){
		return db.getIdol(this);
	}
	
	public void deleteIdol(Author element){
		db.deleteIdol(this , element);
	}
	
	
	public String getName() {
		return db.get(this,"name");
	}
	
	public void setName(String name) {
		db.set(this,"name",name);
	}
	
}	

