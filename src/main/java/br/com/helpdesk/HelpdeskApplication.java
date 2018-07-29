package br.com.helpdesk;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.enums.ProfileEnum;
import br.com.helpdesk.api.service.UserService;

@SpringBootApplication
public class HelpdeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelpdeskApplication.class, args);
	}
	
	CommandLineRunner init(UserService userService, PasswordEncoder passwordEncoder) {
		return args -> {
			initUser(userService, passwordEncoder);
		};
	}
	
	//Criando usuário admin, na inicialização da aplicação.
	private void initUser(UserService userService, PasswordEncoder passwordEncoder) {
		User admin = new User();
		admin.setEmail("admin@helpdesk.com");
		admin.setPassword(passwordEncoder.encode("123456"));
		admin.setProfileEnum(ProfileEnum.ROLE_ADMIN);
		
		User find = userService.findByEmail(admin.getEmail());
		if(find == null) {
			userService.createOrUpdate(find);
		}
	}
}
