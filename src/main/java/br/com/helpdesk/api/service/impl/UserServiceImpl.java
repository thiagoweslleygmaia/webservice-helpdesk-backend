package br.com.helpdesk.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.repository.UserRepository;
import br.com.helpdesk.api.service.UserService;
import br.com.helpdesk.api.service.exception.UserServiceException;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findByEmail(String email) throws UserServiceException {
		try {
			return userRepository.findByEmail(email);
		} catch (Exception e) {
			throw new UserServiceException("Erro ao buscar email do usuário.");
		}
	}

	@Override
	public User createOrUpdate(User user) throws UserServiceException {
		try {
			return userRepository.save(user);
		} catch (Exception e) {
			throw new UserServiceException("Erro ao salvar usuário.");
		}
	}

	@Override
	public User findById(String id) throws UserServiceException {
		try {
			return userRepository.findOne(id);
		} catch (Exception e) {
			throw new UserServiceException("Erro ao buscar usuário.");
		}
	}

	@Override
	public void delete(String id) throws UserServiceException {
		try {
			userRepository.delete(id);
		} catch (Exception e) {
			throw new UserServiceException("Erro ao deletar usuário.");
		}
	}

	@Override
	public Page<User> findAll(int page, int count) throws UserServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return userRepository.findAll(pages);
		} catch (Exception e) {
			throw new UserServiceException("Erro ao listar usuários.");
		}
	}
}
