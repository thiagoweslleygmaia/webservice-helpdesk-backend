package br.com.helpdesk.api.util;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;

import br.com.helpdesk.api.response.Response;

public class RestUtil {

	public static ResponseEntity<?> ok(Object obj) {
		Response<Object> resp = new Response<>();
		resp.setData(obj);
		return ResponseEntity.ok(resp);
	}
	
	public static ResponseEntity<?> errors(List<ObjectError> listErros) {
		Response<Object> resp = new Response<>();
		listErros.forEach(err -> {
			resp.getErrors().add(err.getDefaultMessage());
		});
		return ResponseEntity.ok(resp);
	}
	
	public static ResponseEntity<?> error(Object obj) {
		Response<Object> resp = new Response<>();
		resp.getErrors().add(obj!=null ? obj.toString() : "");
		return ResponseEntity.ok(resp);
	}
}
