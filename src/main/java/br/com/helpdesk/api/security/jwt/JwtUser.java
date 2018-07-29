package br.com.helpdesk.api.security.jwt;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * JwtUser.java
 * Classe auxiliar utilizada para conter as informações do usuário
 * que será utilizado para realizar o parse para o Spring Security.
 */
public class JwtUser implements UserDetails {

	private static final long serialVersionUID = -8357219260192182422L;
	
	private final String id;
	private final String userName;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public JwtUser(String id, String userName, String password, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.authorities = authorities;
	}

	@JsonIgnore
	public String getId() {
		return id;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
