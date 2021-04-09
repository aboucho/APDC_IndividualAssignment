package pt.unl.fct.di.apdc.myproject.util;

import java.util.UUID;

public class AuthToken {

	public static final long EXPIRATION_TIME = 1000 * 60 * 20 ; // 20 min

	public String userId;
	public String role;
	public String tokenID;
	public long creationData;
	public long expirationData;

	public AuthToken(){   }
	
	public AuthToken(String userId,String role) {
		this.userId = userId;
		this.role=role;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}
	
	public boolean expired() {
		if(expirationData<System.currentTimeMillis()) {
			return true;
		}
		else {
			return false;
		}
		
	}
}
