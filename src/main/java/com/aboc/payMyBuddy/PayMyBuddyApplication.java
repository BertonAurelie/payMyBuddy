package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.model.UserDb;
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
		Iterable<UserDb> users = userService.getUsers();
		users.forEach(user -> System.out.println(user.getUsername()));

		Optional<UserDb> userId = userService.getUserById(1);
		UserDb userDb = userId.get();

		UserDb userDbCreate = new UserDb();
		userDbCreate.setUsername("emma92");
		userDbCreate.setEmail("emma.leroy@gmail.com");
		userDbCreate.setPassword("motDePasseSecurise!");
		userDbCreate.setSolde(new BigDecimal("250.75"));
		//User userSave = userService.createUser(userCreate);
	}
}
