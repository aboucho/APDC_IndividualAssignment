package pt.unl.fct.di.apdc.myproject.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateData {
	
	public String userId;
	public String userId2;
	public String new_username;
	public String new_password;
	public String new_password_confirmation;
	public String new_email;
	public String profile;
	public String phone;
	public String mobile_phone;
	public String address;
	public String addressC;
	public String location;
	public String zip_code;
	public String new_role_or_state;
	
	public UpdateData() {
		
	}
	
	public UpdateData(String userId,String new_username,String new_email,String new_password,String new_password_confirmation,
		String profile,String phone,String mobile_phone,String address,String addressC, String location,String zip_code	) {
		
		this.userId=userId;
		this.new_username=new_username;
		this.new_email=new_email;
		this.new_password=new_password;
		this.new_password_confirmation=new_password_confirmation;
		this.profile=profile;
		this.phone=phone;
		this.mobile_phone=mobile_phone;
		this.address=address;
		this.addressC=addressC;
		this.location=location;
		this.zip_code=zip_code;
	}
	
	public UpdateData(String userId,String userId2,String new_role_or_state) {
		this.userId=userId;
		this.userId2=userId2;
		this.new_role_or_state=new_role_or_state;
	}
	
	public boolean validationData() {
		if (!checkNulls() && new_username.length() >= 5) {
			if (new_password.length() >= 5 && new_password.length() <= 15
					&& new_password_confirmation.equals(new_password) && passwordCheck(new_password)) {
				if (profile.toLowerCase().equals("public") || profile.toLowerCase().equals("private")) {
					if (emailCheck(new_email)) {
						if (phoneCheck(phone) && mobileCheck(mobile_phone)) {
							if (zipCheck(zip_code)) {
								return true;
							} else
								return false;
						} else
							return false;
					} else
						return false;
				} else
					return true;
			} else
				return false;
		} else
			return false;
	}
	
	public boolean checkNulls() {
		if(new_username.equals(null) || new_email.equals(null) || new_password.equals(null) || new_password_confirmation.equals(null) || profile.equals(null)  || phone.equals(null) ) {
			if(mobile_phone.equals(null) ||  address.equals(null) || addressC.equals(null) || location.equals(null)  || zip_code.equals(null) ) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	private boolean passwordCheck(String password) {
		Pattern symbols = Pattern.compile("[^A-Za-z0-9]",Pattern.CASE_INSENSITIVE);
		Pattern numbers = Pattern.compile("[0-9]",Pattern.CASE_INSENSITIVE);
		
		Matcher hasSymbols = symbols.matcher(password);
		Matcher hasNumbers = numbers.matcher(password);
		
		return hasSymbols.find() && hasNumbers.find();
	}
	
	private boolean emailCheck(String email) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9]+@([a-zA-Z]+.)+(com|edu|pt|org|gov|mil|net|int)$",Pattern.CASE_INSENSITIVE);
		Matcher good =p.matcher(email);
		
		return good.find();
	}
	
	private boolean phoneCheck(String phone) {
		Pattern p = Pattern.compile("^(\\+351) [0-9]{9}$",Pattern.CASE_INSENSITIVE);
		Matcher good = p.matcher(phone);
		
		return good.find();
	}
	
	private boolean mobileCheck(String mobile) {
		Pattern p = Pattern.compile("^(\\+351) (91|93|96)[0-9]{7}$",Pattern.CASE_INSENSITIVE);
		Matcher good = p.matcher(mobile);
		
		return good.find();
	}
	
	private boolean zipCheck(String zipcode) {
		Pattern p = Pattern.compile("^[0-9]{4}-[0-9]{3}$",Pattern.CASE_INSENSITIVE);
		Matcher good = p.matcher(zipcode);
		
		return good.find();
	}
}
