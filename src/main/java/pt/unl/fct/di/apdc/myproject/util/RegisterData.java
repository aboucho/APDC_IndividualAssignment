package pt.unl.fct.di.apdc.myproject.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterData {
	
	public String userId;
	public String username;
	public String password;
	public String password_confirmation;
	public String email;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String username,String userId,String email,String password,String password_confirmation) {
		this.userId=username;
		this.password=password;
		this.username=username;
		this.email=email;
		this.password_confirmation=password_confirmation;
	}
	
	public boolean validationData() {
		if (!checkNulls() && username.length() >= 5 && userId.length() >= 5) {
			if (password.length() >= 5 && password.length() <= 15 && passwordCheck(password)
					&& password_confirmation.equals(password)) {
				if (emailCheck(email)) {
					return true;
				} else
					return false;
			} else
				return false;
		} else
			return false;
	}
	
	private boolean checkNulls() {
		if(userId.equals(null) || password.equals(null) || username.equals(null)  || password.equals(null)  || email.equals(null) || password_confirmation.equals(null)) {
			return true;
		}
		else return false;
	}

	private boolean passwordCheck(String password) {
		Pattern symbols = Pattern.compile("[^A-Za-z0-9]",Pattern.CASE_INSENSITIVE);
		Pattern numbers = Pattern.compile("[0-9]",Pattern.CASE_INSENSITIVE);
		
		Matcher hasSymbols = symbols.matcher(password);
		Matcher hasNumbers = numbers.matcher(password);
		
		return hasSymbols.find() && hasNumbers.find();
	}
	
	private boolean emailCheck(String email) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]+@([a-zA-Z]+.)+(com|edu|pt|org|gov|mil|net|int)$");
		
		Matcher good =p.matcher(email);
		
		return good.find();
	}
	
}
