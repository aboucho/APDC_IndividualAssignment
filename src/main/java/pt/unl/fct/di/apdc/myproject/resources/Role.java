package pt.unl.fct.di.apdc.myproject.resources;

public enum Role {
	USER("USER", 0), GBO("GBO", 1), GA("GA", 2), SU("SU", 3);

	public final String name;

	public final int value;

	Role(String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public int level() {
		return value;
	}
	
	
}