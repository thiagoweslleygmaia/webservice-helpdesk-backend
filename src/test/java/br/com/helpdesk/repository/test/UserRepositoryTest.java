package br.com.helpdesk.repository.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.helpdesk.api.entity.User;
import br.com.helpdesk.api.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void testSuccessFindByEmail() {
//		User user = new User();
//		user.setEmail("admin@helpdesk.com");
//		user.setPassword("123456");
//		user.setProfileEnum(ProfileEnum.ROLE_ADMIN);
//		User save = userRepository.save(user);
		
		List<User> list = userRepository.findAll();
		list.forEach(obj -> {
			System.out.println(obj.getId());
			System.out.println(obj.getEmail());
			System.out.println(obj.getPassword());
			System.out.println(obj.getProfileEnum());
		});
		
		User user = userRepository.findByEmail("admin@helpdesk.com");
		Assert.assertEquals("admin@helpdesk.com", user.getEmail());
	}
}
