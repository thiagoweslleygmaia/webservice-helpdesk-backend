package br.com.helpdesk.api.security.jwt;

import java.io.Serializable;

/**
 * JwtAuthenticationRequest.java
 * Classe utilizada para o login do usuário.
 */
public class JwtAuthenticationRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	
	public JwtAuthenticationRequest() {
		super();
	}
	
	public JwtAuthenticationRequest(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
