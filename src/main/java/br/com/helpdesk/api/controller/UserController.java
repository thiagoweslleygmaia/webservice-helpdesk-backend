package br.com.helpdesk.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.service.UserService;
import br.com.helpdesk.api.util.RestUtil;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins="*")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> create(HttpServletRequest request, @RequestBody User user, BindingResult result) {
		try {
			validateCreateUser(user, result);
			if(result.hasErrors()) {
				return RestUtil.errors(result.getAllErrors());
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user = userService.createOrUpdate(user);
			return RestUtil.ok(user);
		} catch (DuplicateKeyException e) {
			return RestUtil.error("E-mail already registered!");
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	private void validateCreateUser(User user, BindingResult result) {
		if(user==null || user.getEmail() == null 
				|| user.getEmail().isEmpty()) {
			result.addError(new ObjectError(User.class.getName(), "Email no information."));
		}
	}
	
	@PutMapping
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> update(HttpServletRequest request, @RequestBody User user, BindingResult result) {
		try {
			validateUpdateUser(user, result);
			if(result.hasErrors()) {
				return RestUtil.errors(result.getAllErrors());
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user = userService.createOrUpdate(user);
			return RestUtil.ok(user);
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	private void validateUpdateUser(User user, BindingResult result) {
		if(user==null || user.getId() == null) {
			result.addError(new ObjectError(User.class.getName(), "Id no information."));
		}
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			result.addError(new ObjectError(User.class.getName(), "Email no information."));
		}
	}
	
	@GetMapping(value="{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> findById(@PathVariable("id") String id) {
		try {
			User user = userService.findById(id);
			if(user == null) {
				return RestUtil.error("Register not found id: "+id);
			} else {
				return RestUtil.ok(user);
			}
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@DeleteMapping(value="{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> delete(@PathVariable("id") String id) {
		try {
			User user = userService.findById(id);
			if(user == null) {
				return RestUtil.error("Register not found id: "+id);
			} else {
				userService.delete(id);
				return RestUtil.ok("Registration successfully deleted.");
			}			
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@GetMapping(value="{page}/{count}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<?> findAll(@PathVariable int page, @PathVariable int count) {
		try {
			Page<User> users = userService.findAll(page, count);
			if(users == null) {
				return RestUtil.error("no records found.");
			} else {
				return RestUtil.ok(users);
			}			
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
}
