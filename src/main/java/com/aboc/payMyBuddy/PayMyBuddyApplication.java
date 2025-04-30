package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.model.User;
import com.aboc.payMyBuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootApplication
public class PayMyBuddyApplication implements CommandLineRunner {
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		Iterable<User> users = userService.getUsers();
		users.forEach(user -> System.out.println(user.getUsername()));

		Optional<User> userId = userService.getUserById(1);
		User user = userId.get();

		User userCreate = new User();
		userCreate.setUsername("emma92");
		userCreate.setEmail("emma.leroy@gmail.com");
		userCreate.setPassword("motDePasseSecurise!");
		userCreate.setSolde(new BigDecimal("250.75"));
		//User userSave = userService.createUser(userCreate);
	}
}
