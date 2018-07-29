package br.com.helpdesk.api.entity;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import br.com.helpdesk.api.enums.ProfileEnum;

@Document
public class User {

	@Id
	private String id;
	
	@Indexed(unique=true)
	@NotBlank(message="Email required")
	@Email(message="email invalid")
	private String email;
	
	@NotBlank(message="Password required")
	@Size(min=6)
	private String password;
	
	private ProfileEnum profileEnum;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ProfileEnum getProfileEnum() {
		return profileEnum;
	}

	public void setProfileEnum(ProfileEnum profileEnum) {
		this.profileEnum = profileEnum;
	}
}