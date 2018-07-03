package wh1spr.tournament;

public class Person {

	private String name = null;
	private String id = null;
	
	public Person(String id, String name) {
		if (id == null && name == null) throw new IllegalArgumentException("No identifier was given.");
		this.id = null;
		this.name = null;
	}
	
	public String getName() {
		if (this.name == null) return this.id;
		return this.name;
	}
	
	public String getId() {
		if (this.id == null) return this.name;
		return this.id;
	}
	
	public void setName(String name) {
		if (this.id == null && name == null) throw new IllegalArgumentException("Name and ID cannot both be NULL.");
		else this.name = name;
	}

	public void setId(String id) {
		if (this.name == null && id == null) throw new IllegalArgumentException("Name and ID cannot both be NULL.");
		else this.id = id;
	}
	
	public static final Person NOONE = new Person("0", "Noone");
	
}
