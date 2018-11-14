package br.com.helpdesk.api.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;
import br.com.helpdesk.api.security.jwt.JwtAuthenticationRequest;
import br.com.helpdesk.api.security.jwt.JwtTokenUtil;
import br.com.helpdesk.api.security.jwt.JwtUser;
import br.com.helpdesk.api.security.jwt.JwtUserFactory;
import br.com.helpdesk.api.security.model.CurrentUser;
import br.com.helpdesk.api.service.UserService;
import br.com.helpdesk.api.service.exception.UserServiceException;
import br.com.helpdesk.api.util.RestUtil;

/**
 * AuthenticationRestController.java
 * Classe utilizada para o controle de autenticação.
 */
@RestController
@CrossOrigin(origins="*") //Permite o acesso de qualquer Porta ou IP.
public class AuthenticationRestController {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/api/auth")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) {
		
		try {
			//Está dando erro de BadCredentialsException na hora de setar a autenticação.
//			final Authentication authentication = authenticationManager.authenticate(
//					new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
//															authenticationRequest.getPassword()));
			
			UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),
														    authenticationRequest.getPassword());
			
			final Authentication authentication = authenticationManager.authenticate(authenticationToken);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (AuthenticationException e) {
			Logger.getLogger(this.getClass()).error(e.getMessage());
		}
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = jwtTokenUtil.generateToken(userDetails);
		
		User emailUser = new User();
		try {
			emailUser = userService.findByEmail(authenticationRequest.getEmail());
		} catch (UserServiceException e) {
			RestUtil.error(e.getMessage());
		}
		
		final User user = emailUser;
		user.setPassword(null);
		return ResponseEntity.ok(new CurrentUser(token, user));
	}
	
	@PostMapping("/api/refresh")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String userName = jwtTokenUtil.getUserNameFromToken(token);
		
		User emailUser = new User();
		try {
			emailUser = userService.findByEmail(userName);
		} catch (UserServiceException e) {
			RestUtil.error(e.getMessage());
		}
		
		final User user = emailUser;
		
		if(jwtTokenUtil.canTokenBeRefreshed(token)) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new CurrentUser(refreshedToken, user));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	//Somente Teste
	@PostMapping("/api/geraToken")
	public ResponseEntity<?> geraToken(@RequestBody JwtAuthenticationRequest authenticationRequest) {
		User user = new User();
		user.setEmail(authenticationRequest.getEmail());
		user.setPassword(authenticationRequest.getPassword());
		user.setProfileEnum(ProfileEnum.ROLE_ADMIN);
		
		JwtUser jwtUser = JwtUserFactory.create(user);
		String generateToken = jwtTokenUtil.generateToken(jwtUser);
		return ResponseEntity.ok(generateToken);
	}
	
	
	
	
	
	
	
	
	
}
