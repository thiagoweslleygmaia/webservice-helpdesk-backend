package br.com.helpdesk.api.security.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;

/**
 * JwtUserFactory.java
 * Classe responsável por transformar um usuário JwtUser
 * em um usuário do Spring Security.
 */
public class JwtUserFactory {

	private JwtUserFactory() {
	}
	
	/**
	 * Método responsável por criar um usuário Jwt
	 * com base nas informações do token.
	 */
	public static JwtUser create(User user) {
		return new JwtUser(user.getId(), 
						   user.getEmail(), 
						   user.getPassword(), 
						   mapToGrantedAuthorities(user.getProfileEnum()));
	}
	
	/**
	 * Método responsável por converter o perfil do usuário
	 * para um formato reconhecido pelo spring security.
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(ProfileEnum profileEnum) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
		return authorities;
	}
}
