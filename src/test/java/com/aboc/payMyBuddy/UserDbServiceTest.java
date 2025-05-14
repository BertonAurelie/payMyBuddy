package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
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
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserDbServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void GivenListOfUsers_WhenGetAllUsers_thenReturnListOfUserName() {
        BigDecimal solde = new BigDecimal(0);
        UserDb userDb1 = new UserDb("test1", "test1@example.com", "password1", solde);
        UserDb userDb2 = new UserDb("test2", "test2@example.com", "password2", solde);
        List<UserDb> userPayMyBuddies = Arrays.asList(userDb1, userDb2);
        List<UserDb> result = new ArrayList<UserDb>();

        when(userRepository.findAll()).thenReturn(userPayMyBuddies);

        Iterable<UserDb> iterable = userService.getUsers();
        iterable.forEach(result::add);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getUsername());
        assertEquals("test2", result.get(1).getUsername());
    }

    @Test
    public void GivenListOfUsers_WhenGetUserById_thenReturnSpecificUser() {
        BigDecimal solde = new BigDecimal(0);
        UserDb userDb1 = new UserDb("test1", "test1@example.com", "password1", solde);
        Optional<UserDb> userId = Optional.of(userDb1);
        List<UserDb> result = new ArrayList<UserDb>();

        when(userRepository.findById(1)).thenReturn(userId);

        Optional<UserDb> optionalUser = userService.getUserById(1);
        optionalUser.ifPresent(result::add);

        assertNotNull(result);
        assertEquals("test1", result.get(0).getUsername());
    }

    @Test
    public void GivenNewUser_whenCreateUser_thenReturnNewUser() {
        BigDecimal solde = new BigDecimal(0);
        CreatedUserDto userDto = new CreatedUserDto();
        userDto.setUsername("test1");
        userDto.setEmail("test1@example.com");
        userDto.setPassword("password1");

        //when(userRepository.createUser(any(CreatedUserDto.class))).thenReturn(1);

        //int result = userService.createUser(userDto);


        //assertEquals(0, result);
    }

}
