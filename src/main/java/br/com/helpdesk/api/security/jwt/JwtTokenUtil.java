package br.com.helpdesk.api.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**	
 * JwtTokenUtil.java
 * Classe utilitária responsável por manipular 
 * as informações do usuário via Jwt.
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "created";
	static final String CLAIM_KEY_EXPIRED = "exp";
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	/**
	 * Método utilizado para obter o usuário do header da requisição,
	 * que nesse caso é o email do usuário.
	 */
	public String getUserNameFromToken(String token) {
		try {
			final Claims claims = getClaimsFromToken(token);
			return claims.getSubject(); //UserName
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Método utilizado para obter a data de expiração do token.
	 */
	public Date getExpirationFromToken(String token) {
		try {
			final Claims claims = getClaimsFromToken(token);
			return claims.getExpiration(); //Data de expiração do token
		} catch (Exception e) {
			return null;
		}
	}
	
	//Gera token somente teste
	public static void main(String[] args) {
		//O token só passa sem essa informação no inicio: Bearer
		//String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBoZWxwZGVzay5jb20iLCJjcmVhdGVkIjoxNTMwMzkwOTUyOTYxLCJleHAiOjE1MzA5OTU3NTJ9.FevVAq6f42Zti51KRNT24SRW5dg5ScpWFaHguTMAlT_cRfe_PNpsX4S_ckQGsL1TSFtM8SBGxoU04sJV4mxbyQ";
		
		User user = new User();
		user.setId("1");
		user.setEmail("admin@helpdesk.com");
		user.setPassword("123");
		user.setProfileEnum(ProfileEnum.ROLE_ADMIN);
		//
		JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
		jwtTokenUtil.secret = "AppHelpDesk";
		jwtTokenUtil.expiration = 604800L;
		
		JwtUser jwtUser = JwtUserFactory.create(user);
		String generateToken = jwtTokenUtil.generateToken(jwtUser);
		System.out.println(generateToken);
	}
	
	/**
	 * Método utilizado para fazer o parse do token JWT
	 * e extrair as informações.
	 */
	private Claims getClaimsFromToken(String token) {
		try {
			Claims claims = Jwts.parser()
							.setSigningKey(secret)
							.parseClaimsJws(token).getBody();
			return claims;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Método que verifica se o token está expirado.
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationFromToken(token);
		return expiration.before(new Date());
	}
	
	/**
	 * Método que gera o Token.
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		
		final Date createDate = new Date();
		claims.put(CLAIM_KEY_CREATED, createDate);
		
		return doGenerateToken(claims);
	}
	
	/**
	 * Método auxiliar na geração do Token.
	 */
	private String doGenerateToken(Map<String, Object> claims) {
		final Date createDate = (Date) claims.get(CLAIM_KEY_CREATED);
		final Date expirationDate = new Date(createDate.getTime() + expiration * 1000);
		return Jwts.builder()
			   .setClaims(claims)
			   .setExpiration(expirationDate)
			   .signWith(SignatureAlgorithm.HS512, secret)
			   .compact();
	}
	
	/**
	 * Método que verifica se o token pode ser atualizado.
	 */
	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token));
	}
	
	/**
	 * Método que atualiza o token.
	 */
	public String refreshToken(String token) {
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, new Date());
			return doGenerateToken(claims);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Método que verifica se o token é válido.
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		JwtUser user = (JwtUser) userDetails;
		final String userName = getUserNameFromToken(token);
		return (userName.equals(user.getUsername())
				&& !isTokenExpired(token));
	}
}
