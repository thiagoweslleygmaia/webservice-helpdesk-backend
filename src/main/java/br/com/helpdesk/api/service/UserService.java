package br.com.helpdesk.api.service;

import org.springframework.data.domain.Page;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.service.exception.UserServiceException;

public interface UserService {

	User findByEmail(String email) throws UserServiceException;
	
	User createOrUpdate(User user) throws UserServiceException;
	
	User findById(String id) throws UserServiceException;
	
	void delete(String id) throws UserServiceException;
	
	Page<User> findAll(int page, int count) throws UserServiceException;
}
