package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.model.User;
import com.aboc.payMyBuddy.repository.UserRepository;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void GivenListOfUsers_WhenGetAllUsers_thenReturnListOfUserName() {
        BigDecimal solde = new BigDecimal(0);
        User user1 = new User(1, "test1", "test1@example.com", "password1", solde);
        User user2 = new User(2, "test2", "test2@example.com", "password2", solde);
        List<User> users = Arrays.asList(user1, user2);
        List<User> result = new ArrayList<User>();

        when(userRepository.findAll()).thenReturn(users);

        Iterable<User> iterable = userService.getUsers();
        iterable.forEach(result::add);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getUsername());
        assertEquals("test2", result.get(1).getUsername());
    }

    @Test
    public void GivenListOfUsers_WhenGetUserById_thenReturnSpecificUser() {
        BigDecimal solde = new BigDecimal(0);
        User user1 = new User(1, "test1", "test1@example.com", "password1", solde);
        Optional<User> userId = Optional.of(user1);
        List<User> result = new ArrayList<User>();

        when(userRepository.findById(1)).thenReturn(userId);

        Optional<User> optionalUser = userService.getUserById(1);
        optionalUser.ifPresent(result::add);

        assertNotNull(result);
        assertEquals("test1", result.get(0).getUsername());
    }

    @Test
    public void GivenNewUser_whenCreateUser_thenReturnNewUser(){
        BigDecimal solde = new BigDecimal(0);
        User newUser = new User(1, "test1", "test1@example.com", "password1", solde);

        when(userRepository.save(any(User.class))).thenReturn(newUser);

        List<User> result = new ArrayList<>();
        User arr = userService.createUser(newUser);
        result.add(arr);

        assertEquals("test1", result.get(0).getUsername());

    }

}
