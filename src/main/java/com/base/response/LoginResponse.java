package com.base.response;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String jwttoken;

	public LoginResponse(String jwttoken) {
		this.jwttoken = jwttoken;
	}

	public String getToken() {
		return this.jwttoken;
	}
}
