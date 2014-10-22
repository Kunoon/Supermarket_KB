package com.koobest.m.authenticate.toolkit;

public class AuthTokenBuilder {
	String authToken = null;

	public AuthTokenBuilder() {
	}

	public void makeAuthToken(String username, String password) {
		authToken = MD5ResultBuilder
				.getMD5((username.toLowerCase() + MD5ResultBuilder
						.getMD5(password.getBytes())).getBytes());
	}

	public String getAuthToken() {
		return authToken;
	}
}
