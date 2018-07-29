package br.com.helpdesk.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import br.com.helpdesk.api.dto.Summary;
import br.com.helpdesk.api.entity.ChangeStatus;
import br.com.helpdesk.api.entity.Ticket;
import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;
import br.com.helpdesk.api.enums.StatusEnum;
import br.com.helpdesk.api.security.jwt.JwtTokenUtil;
import br.com.helpdesk.api.service.TicketService;
import br.com.helpdesk.api.service.UserService;
import br.com.helpdesk.api.service.exception.TicketServiceException;
import br.com.helpdesk.api.util.MethodsUtils;
import br.com.helpdesk.api.util.RestUtil;

@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins="*")
public class TicketController {

	@Autowired
	private TicketService ticketService;
	@Autowired
	protected JwtTokenUtil jwtTokenUtil;
	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')") //only for the profiles of client.
	public ResponseEntity<?> createOrUpdate(HttpServletRequest request, @RequestBody Ticket ticket, BindingResult result) {
		try {
			validateCreateTicket(ticket, result);
			if(result.hasErrors()) {
				return RestUtil.errors(result.getAllErrors());
			}
			ticket.setStatus(StatusEnum.New);
			ticket.setUser(userFromRequest(request));
			ticket.setDate(new Date());
			ticket.setNumber(generateNumber());
			
			ticket = ticketService.createOrUpdate(ticket);
			return RestUtil.ok(ticket);
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	private Integer generateNumber() {
		return new Random().nextInt(9999);
	}

	private void validateCreateTicket(Ticket ticket, BindingResult result) {
		if(MethodsUtils.isNull(ticket)
				|| MethodsUtils.isNullOrEmpty(ticket.getTitle())) {
			result.addError(new ObjectError("Ticket", "Title no information."));
			return;
		}
	}
	
	private User userFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		String email = jwtTokenUtil.getUserNameFromToken(token);
		return userService.findByEmail(email);
	}
	//////////////////////////////////////////////////////////////////////
	
	@PutMapping
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<?> update(@RequestBody Ticket ticket, BindingResult result) {
		try {
			validateUpdateTicket(ticket, result);
			if(result.hasErrors()) {
				return RestUtil.errors(result.getAllErrors());
			}
			//Status anterior salvo no banco.
			Ticket ticketCurrent = ticketService.findById(ticket.getId());
			
			ticket.setStatus(ticketCurrent.getStatus());
			ticket.setUser(ticketCurrent.getUser());
			ticket.setDate(ticketCurrent.getDate());
			ticket.setNumber(ticketCurrent.getNumber());
			//verifica se o ticket já foi atribuido.
			if(!MethodsUtils.isNull(ticketCurrent.getAssignedUser())) {
				ticket.setAssignedUser(ticketCurrent.getAssignedUser());
			}
			Ticket OutTicket = ticketService.createOrUpdate(ticket);
			return RestUtil.ok(OutTicket);
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	private void validateUpdateTicket(Ticket ticket, BindingResult result) {
		if(MethodsUtils.isNull(ticket)
				|| MethodsUtils.isNullOrEmpty(ticket.getId())) {
			result.addError(new ObjectError("Ticket", "Id no information."));
		}
		if(MethodsUtils.isNullOrEmpty(ticket.getTitle())) {
			result.addError(new ObjectError("Ticket", "Title no information."));
		}
	}
	//////////////////////////////////////////////////////////////////////
	
	@GetMapping(value="{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')") //only for the profiles of client and technician.
	public ResponseEntity<?> findById(@PathVariable String id) {
		try {
			Ticket ticket = ticketService.findById(id);
			if(MethodsUtils.isNullOrEmpty(ticket)) {
				return RestUtil.error("Register not found id: "+id);
			}
			ticket.setChanges(new ArrayList<ChangeStatus>());
			Iterable<ChangeStatus> listChangeStatus = ticketService.listChangeStatus(ticket.getId());
			listChangeStatus.forEach(chanStatus -> {
				chanStatus.setTicket(null);
				ticket.getChanges().add(chanStatus);
			});
			return RestUtil.ok(ticket);
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@DeleteMapping(value="{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER')")
	public ResponseEntity<?> delete(@PathVariable String id) {
		try {
			Ticket ticket = ticketService.findById(id);
			if(MethodsUtils.isNullOrEmpty(ticket)) {
				return RestUtil.error("Register not found id: "+id);
			}
			ticketService.delete(id);
			return RestUtil.ok("Registration successfully deleted.");
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@GetMapping(value="{page}/{count}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<?> findAll(HttpServletRequest request, @PathVariable int page, @PathVariable int count) {
		try {
			Page<Ticket> listTickets = null;
			User userRequest = userFromRequest(request);
			if(!MethodsUtils.isNull(userRequest)
					&& !MethodsUtils.isNull(userRequest.getProfileEnum())) {
				
				if(userRequest.getProfileEnum().equals(ProfileEnum.ROLE_TECHNICIAN)) {
					listTickets = ticketService.listTicket(page, count);
				} else if(userRequest.getProfileEnum().equals(ProfileEnum.ROLE_CUSTOMER)) {
					listTickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
				}
			}
			
			if(MethodsUtils.isNull(listTickets)) {
				return RestUtil.error("No records found.");
			} else {
				return RestUtil.ok(listTickets);
			}
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@GetMapping(value="{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<?> findByParams(HttpServletRequest request, @PathVariable("page") int page, 
										  @PathVariable("count") int count, @PathVariable("number") Integer number,
										  @PathVariable("title") String title, @PathVariable("status") String status,
										  @PathVariable("priority") String priority, @PathVariable("assigned") Boolean assigned) {
		
		try {
			title = title.equals("uninformed") ? "" : title;
			status = status.equals("uninformed") ? "" : status;
			priority = priority.equals("uninformed") ? "" : priority;
			
			Page<Ticket> listTickets = null;
			if(number > 0) {
				listTickets = ticketService.findByNumber(page, count, number);
			} else {
				User userRequest = userFromRequest(request);
				if(!MethodsUtils.isNull(userRequest)
						&& !MethodsUtils.isNull(userRequest.getProfileEnum())) {
					
					if(userRequest.getProfileEnum().equals(ProfileEnum.ROLE_TECHNICIAN)) {
						if(assigned) {
							listTickets = ticketService.findByParameterAndAssignedUser(page, count, title, status, priority, userRequest.getId());
						} else {
							listTickets = ticketService.findByParameters(page, count, title, status, priority);
						}
					} else if(userRequest.getProfileEnum().equals(ProfileEnum.ROLE_CUSTOMER)) {
						ticketService.findByParametersAndCurrentUser(page, count, title, status, priority, userRequest.getId());
					}
				}
			}
			
			if(!MethodsUtils.isNullOrEmpty(listTickets)) {
				return RestUtil.ok(listTickets);
			} else {
				return RestUtil.error("No records found.");
			}
		} catch (TicketServiceException e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	@PutMapping(value="{id}/{status}")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<?> changeStatus(HttpServletRequest request, @PathVariable("id") String id, 										  
										  @PathVariable("status") String status, @RequestBody Ticket ticket,
										  BindingResult result) {
		
		try {
			validateChangeStatus(id, status, result);
			if(result.hasErrors()) {
				return RestUtil.errors(result.getAllErrors());
			}
			Ticket ticketCurrent = ticketService.findById(id);
			ticketCurrent.setStatus(StatusEnum.getStatus(status));
			
			User userRequest = userFromRequest(request);
			if("Assigned".equals(status)) {
				ticketCurrent.setAssignedUser(userRequest);
			}						
			ticketCurrent = ticketService.createOrUpdate(ticketCurrent);
			
			ChangeStatus changeStatus = new ChangeStatus();
			changeStatus.setUserChange(userRequest);
			changeStatus.setDateChangeStatus(new Date());
			changeStatus.setStatus(StatusEnum.getStatus(status));
			changeStatus.setTicket(ticketCurrent);
			ticketService.createChangeStatus(changeStatus);
			return RestUtil.ok(ticketCurrent);
		} catch (Exception e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
	private void validateChangeStatus(String id, String status, BindingResult result) {
		if(MethodsUtils.isNullOrEmpty(id)) {
			result.addError(new ObjectError("Ticket", "Id no information."));
		}
		if(MethodsUtils.isNullOrEmpty(status)) {
			result.addError(new ObjectError("Ticket", "Status no information."));
		}
	}
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	@GetMapping(value="/summary")
	@PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
	public ResponseEntity<?> findSummary() {
		
		Integer amountNew=0,amountResolved=0,amountApproved=0,
				amountDisapproved=0,amountAssigned=0,amountClosed=0;
		
		try {
			Iterable<Ticket> listTickets = ticketService.findAll();
			if(!MethodsUtils.isNullOrEmpty(listTickets)) {
				for (Ticket tick : listTickets) {
					if(tick.getStatus().equals(StatusEnum.New)) {
						amountNew++;
					}
					if(tick.getStatus().equals(StatusEnum.Resolved)) {
						amountResolved++;
					}
					if(tick.getStatus().equals(StatusEnum.Approvad)) {
						amountApproved++;
					}
					if(tick.getStatus().equals(StatusEnum.Disapprovad)) {
						amountDisapproved++;
					}
					if(tick.getStatus().equals(StatusEnum.Assigned)) {
						amountAssigned++;
					}
					if(tick.getStatus().equals(StatusEnum.Clesed)) {
						amountClosed++;
					}
				}
			}
			
			Summary summary = new Summary(amountNew, amountResolved, amountApproved, 
									      amountDisapproved, amountAssigned, amountClosed);
			
			return RestUtil.ok(summary);
		} catch (TicketServiceException e) {
			return RestUtil.error(e.getMessage());
		}
	}
	
}
