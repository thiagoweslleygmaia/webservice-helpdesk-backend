package br.com.helpdesk.api.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import br.com.helpdesk.api.entity.ChangeStatus;
import br.com.helpdesk.api.entity.Ticket;
import br.com.helpdesk.api.service.exception.TicketServiceException;

@Component
public interface TicketService {

	Ticket createOrUpdate(Ticket ticket) throws TicketServiceException;
	
	Ticket findById(String id) throws TicketServiceException;
	
	void delete(String id) throws TicketServiceException;
	
	Page<Ticket> listTicket(int page, int count) throws TicketServiceException;
	
	ChangeStatus createChangeStatus(ChangeStatus changeStatus) throws TicketServiceException;
	
	Iterable<ChangeStatus> listChangeStatus(String ticketId) throws TicketServiceException;
	
	Page<Ticket> findByCurrentUser(int page, int count, String userId) throws TicketServiceException;
	
	Page<Ticket> findByParameters(int page, int count, String title, String status, String priority) throws TicketServiceException;
	
	Page<Ticket> findByParametersAndCurrentUser(int page, int count, String title, String status, String priority, String userId) throws TicketServiceException;
	
	Page<Ticket> findByNumber(int page, int count, Integer number) throws TicketServiceException;
	
	Iterable<Ticket> findAll() throws TicketServiceException;
	
	Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser) throws TicketServiceException;
}
