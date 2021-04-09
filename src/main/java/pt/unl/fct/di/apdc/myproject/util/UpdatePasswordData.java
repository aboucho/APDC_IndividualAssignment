package pt.unl.fct.di.apdc.myproject.util;

public class UpdatePasswordData {

	public String userId;
	public String password;
	public String new_password;
	public String new_password_confirmation;

	public UpdatePasswordData() {

	}

	public UpdatePasswordData(String userId, String password, String new_password, String new_password_confirmation) {
		this.userId = userId;
		this.password = password;
		this.new_password = new_password;
		this.new_password_confirmation = new_password_confirmation;
	}
   
	public boolean validationData() {
		if (new_password.equals(new_password_confirmation) && !checkNulls()) {
			return true;
		} else {
			return false;
		}
	}
	
		public boolean checkNulls() {
			if (userId.equals(null) || password.equals(null) || new_password.equals(null)
					|| new_password_confirmation.equals(null)) {
				return true;
			} else {
				return false;
			}
		}
}
