package pt.unl.fct.di.apdc.myproject.util;

public class DeleteData {
	
	public String userId;
	public String userId2;
	
	public DeleteData() {
	}
	
	public DeleteData(String userId,String userId2) {
		this.userId=userId;
		this.userId2=userId2;
	}
	
	public boolean validationData() {
		if(userId.equals(null) || userId2.equals(null)) {
			return false;
		}
		else return true;
	}
}