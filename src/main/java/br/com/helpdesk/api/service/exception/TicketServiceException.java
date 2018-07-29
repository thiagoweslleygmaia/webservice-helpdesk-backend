package br.com.helpdesk.api.service.exception;

public class TicketServiceException extends Exception {

	private static final long serialVersionUID = 7754663429342502881L;

	public TicketServiceException(String message) {
		super(message);
	}
}
