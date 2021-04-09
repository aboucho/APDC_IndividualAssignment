package pt.unl.fct.di.apdc.myproject.util;

public class LoginData {
	
	public String userId;
	public String password;
	
	public LoginData() {
		
	}
	
	public LoginData(String userId,String password) {
		this.userId=userId;
		this.password=password;
	}
	
	public LoginData(String userId) {
		this.userId=userId;
	}
	
	
}
