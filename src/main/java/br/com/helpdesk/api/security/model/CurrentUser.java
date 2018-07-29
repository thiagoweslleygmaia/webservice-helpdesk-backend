package br.com.helpdesk.api.security.model;

import br.com.helpdesk.api.entity.User;

/**
 * CurrentUser.java
 * Classe utilizada para retornar o token 
 * para o usuário durante o login.
 */
public class CurrentUser {

	private String token;
	private User user;
	
	public CurrentUser(String token, User user) {
		this.token = token;
		this.user = user;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
