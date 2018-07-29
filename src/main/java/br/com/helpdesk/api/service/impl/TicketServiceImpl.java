package br.com.helpdesk.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.helpdesk.api.entity.ChangeStatus;
import br.com.helpdesk.api.entity.Ticket;
import br.com.helpdesk.api.repository.ChangeStatusRepository;
import br.com.helpdesk.api.repository.TicketRepository;
import br.com.helpdesk.api.service.TicketService;
import br.com.helpdesk.api.service.exception.TicketServiceException;

@Service
public class TicketServiceImpl implements TicketService {
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
	private ChangeStatusRepository changeStatusRepository;

	@Override
	public Ticket createOrUpdate(Ticket ticket) throws TicketServiceException {
		try {
			return ticketRepository.save(ticket);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao salvar ticket.");
		}
	}

	@Override
	public Ticket findById(String id) throws TicketServiceException {
		try {
			return ticketRepository.findOne(id);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao pesquisar ticket.");
		}
	}

	@Override
	public void delete(String id) throws TicketServiceException {
		try {
			ticketRepository.delete(id);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao deletar ticket.");
		}
	}

	@Override
	public Page<Ticket> listTicket(int page, int count) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findAll(pages);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao listar ticketes.");
		}
	}

	@Override
	public ChangeStatus createChangeStatus(ChangeStatus changeStatus) throws TicketServiceException {
		try {
			return changeStatusRepository.save(changeStatus);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao salvar status.");
		}
	}

	@Override
	public Iterable<ChangeStatus> listChangeStatus(String ticketId) throws TicketServiceException {
		try {
			return changeStatusRepository.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao listar status.");
		}
	}

	@Override
	public Page<Ticket> findByCurrentUser(int page, int count, String userId) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findByUserIdOrderByDateDesc(pages, userId);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao buscar usuário.");
		}
	}

	@Override
	public Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDesc(title, status, priority, pages);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao buscar as informações do ticket.");
		}
	}

	@Override
	public Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority, String userId) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDesc(title, status, priority, pages);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao buscar as informações do ticket por usuário.");
		}
	}

	@Override
	public Page<Ticket> findByNumber(int page, int count, Integer number) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findByNumber(number, pages);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao buscar o número do ticket.");
		}
	}

	@Override
	public Iterable<Ticket> findAll() throws TicketServiceException {
		try {
			return ticketRepository.findAll();
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao listar os ticketes.");
		}
	}

	@Override
	public Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser) throws TicketServiceException {
		try {
			Pageable pages = new PageRequest(page, count);
			return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDesc(title, status, priority, assignedUser, pages);
		} catch (Exception e) {
			throw new TicketServiceException("Erro ao listar os ticketes por técnicos.");
		}
	}

}
