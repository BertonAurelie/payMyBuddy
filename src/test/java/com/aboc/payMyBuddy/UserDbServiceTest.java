package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.Transaction;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.model.dto.request.TransactionDto;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.TransactionRepository;
import com.aboc.payMyBuddy.repository.UserRepository;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDbServiceTest {

    @Mock
    private UserRepository userRepository;


    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, transactionRepository, passwordEncoder);
    }

    @Test
    public void givenUser_WhenCreateUser_thenReturnUser() {
        CreatedUserDto userDto = createdUserDto();

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("hashedPassword");

        when(userRepository.createUser(userDto.getUsername(), userDto.getEmail(), "hashedPassword")).thenReturn(1);

        int result = userService.createUser(userDto);

        assertEquals(1, result);
        Mockito.verify(userRepository).createUser(
                userDto.getUsername(),
                userDto.getEmail(),
                "hashedPassword");
    }

    @Test
    public void givenUserWithNameAlreadyKnown_WhenCreateUser_ThenReturnCreatingUserFailed() {
        CreatedUserDto userDto = createdUserDto();

        // Simule qu’un utilisateur existe déjà avec cet email
        UserDb existingUser = user();
        Mockito.when(userRepository.findUserByEmail(userDto.getEmail())).thenReturn(existingUser);

        // Act & Assert
        RequestException thrown = assertThrows(RequestException.class, () -> userService.createUser(userDto));

        assertEquals("email already existing", thrown.getMessage());

    }

    @Test
    public void givenUsernameAlreadyExists_WhenCreateUser_ThenThrowsException() {
        CreatedUserDto userDto = createdUserDto();

        UserDb existingUser = user();

        when(userRepository.findUserByUsername(userDto.getUsername())).thenReturn(existingUser);

        RequestException thrown = assertThrows(RequestException.class, () -> userService.createUser(userDto));
        assertEquals("username already existing", thrown.getMessage());
    }

    @Test
    public void givenNewUser_whenCreateUser_thenReturnNewUser() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setEmail("newemail@test.com");
        userDto.setUsername("newUsername");
        userDto.setPassword("newPassword");

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.updateUser(anyInt(), anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(1);

        int result = userService.updateUser(userDto);

        assertEquals(1, result);
        verify(userRepository).updateUser(
                eq(1),
                eq("newUsername"),
                eq("newemail@test.com"),
                eq("hashedPassword"),
                anyDouble()
        );
    }

    @Test
    public void givenMoneyToDeposit_whenDepositMoney_thenReturnUserUpdatingWithNewSoldeMoney() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setSolde(4.5);

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.updateUser(anyInt(), anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(1);

        int result = userService.moneyDeposit(userDto, 0);

        assertEquals(1, result);
        verify(userRepository).updateUser(
                anyInt(),
                anyString(),
                anyString(),
                anyString(),
                eq(7.5)
        );
    }

    @Test
    public void givenAmountToRetrait_whenMoneyDepositMethod_thenReturnNewSoldeAfterRetrait() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setSolde(2.5);

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.updateUser(anyInt(), anyString(), anyString(), anyString(), anyDouble()))
                .thenReturn(1);

        int result = userService.moneyDeposit(userDto, 1);

        assertEquals(1, result);
        verify(userRepository).updateUser(
                anyInt(),
                anyString(),
                anyString(),
                anyString(),
                eq(0.5)
        );
    }

    @Test
    public void givenUserWithSolde_whenMoneyDepositMethod_thenReturnThrowNewExceptionInsufficientFunds() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setSolde(12);

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);

        RequestException thrown = assertThrows(RequestException.class, () -> userService.moneyDeposit(userDto, 1));
        assertEquals("insufficient funds", thrown.getMessage());
    }

    @Test
    public void givenUserToAddFriend_whenAddFriendMethod_thenReturnSuccessfullyUserFriend() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setUsername("friend");
        userDto.setEmail("friend@test.com");

        UserDb friend = new UserDb();
        friend.setUsername("friend");
        friend.setEmail("friend@test.com");

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.findUserByEmail("friend@test.com")).thenReturn(friend);

        userService.addFriend(userDto);

        assertEquals(1, user.getFriends().size());
    }

    @Test
    public void givenUnknownUser_whenAddFriend_thenThrowException() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setUsername("friend");
        userDto.setEmail("friend@test.com");

        UserDb friend = new UserDb();
        friend.setUsername("friend");
        friend.setEmail("friend@test.com");

        UserDb user = user();

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        RequestException thrown = assertThrows(RequestException.class, () -> userService.addFriend(userDto));
        assertEquals("unknown user", thrown.getMessage());
    }

    @Test
    public void givenUnknownUser_whenAddFriend_thenThrowExceptionAlreadyAFriend() {
        UpdatedUserDto userDto = new UpdatedUserDto();
        userDto.setUsername("friend");
        userDto.setEmail("friend@test.com");

        UserDb friend = new UserDb();
        friend.setUsername("friend");
        friend.setEmail("friend@test.com");

        Set<UserDb> friends = new HashSet<>();
        friends.add(friend);

        UserDb user = user();
        user.setFriends(friends);

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.findUserByEmail("friend@test.com")).thenReturn(friend);

        RequestException thrown = assertThrows(RequestException.class, () -> userService.addFriend(userDto));
        assertEquals("already a friend", thrown.getMessage());
    }

    @Test
    public void givenUserWithFriendsList_whenShowListFriendsMethod_thenReturnFriendsListOfTheUserGiven() {
        UserDb friend = new UserDb();
        friend.setUsername("friend");
        friend.setEmail("friend@test.com");

        Set<UserDb> friends = new HashSet<>();
        friends.add(friend);

        UserDb user = user();
        user.setFriends(friends);

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        when(userRepository.findUserById(1)).thenReturn(user);

        List<String> listFriends = userService.showListFriends();

        assertEquals(1, listFriends.size());
    }

    @Test
    public void givenTwoUser_WhenOneUserShouldSendMoneyToTheSecond_thenReturnSuccessfullySending() {
        UserDb friend1 = new UserDb("friend", "friend@test.com", "password", 10.5);
        UserDb friend2 = new UserDb("friend2", "friend2@test.com", "password", 6.2);

        Set<UserDb> friends = new HashSet<>();
        friends.add(friend1);
        friends.add(friend2);

        UserDb user = user();
        user.setFriends(friends);
        user.setSolde(15);

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        TransactionDto transactionDto = new TransactionDto("friend", "description", 5.0);

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.findUserByUsername("friend")).thenReturn(friend1);

        userService.sendMoney(transactionDto);

        assertEquals(9.98, user.getSolde());
        assertEquals(15.5, friend1.getSolde());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    public void givenTwoUser_WhenOneUserShouldSendMoney_thenReturnThrowNewExceptionWithInsufficientFunds() {
        UserDb friend1 = new UserDb("friend", "friend@test.com", "password", 10.5);

        Set<UserDb> friends = new HashSet<>();
        friends.add(friend1);

        UserDb user = user();
        user.setFriends(friends);

        CustomUserDetails customUserDetails = new CustomUserDetails(user, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        TransactionDto transactionDto = new TransactionDto("friend", "description", 5.0);

        when(userRepository.findUserById(1)).thenReturn(user);
        when(userRepository.findUserByUsername("friend")).thenReturn(friend1);

        RequestException thrown = assertThrows(RequestException.class, () -> userService.sendMoney(transactionDto));
        assertEquals("insufficient funds", thrown.getMessage());
    }

    @Test
    public void givenListOfTransaction_whenShowTransactionMethod_thenReturnListOfTransaction() {
        UserDb friend1 = new UserDb("friend", "friend@test.com", "password", 10.5);
        UserDb friend2 = new UserDb("friend2", "friend2@test.com", "password", 6.2);

        Set<UserDb> friends = new HashSet<>();
        friends.add(friend1);
        friends.add(friend2);

        UserDb user = user();
        user.setFriends(friends);

        CustomUserDetails customUserDetails = new CustomUserDetails(user(), List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );

        Transaction transaction1 = new Transaction(user, friend1, "montant", 5, 0.025);
        Transaction transaction2 = new Transaction(user, friend2, "montant2", 16, 0.08);
        transaction2.setId(1);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction1);
        transactionList.add(transaction2);

        when(transactionRepository.findTransactionBySenderId(1)).thenReturn(transactionList);

        List<Transaction> result = userService.showTransaction();
        var list = result.stream()
                .toList();

        assertEquals(2, result.size());
        assertEquals("USER", user.getRole());
        assertEquals(user, list.get(0).getSender());
        assertEquals(friend1, list.get(0).getReceiver());
        assertEquals("montant", list.get(0).getDescription());
        assertEquals(16, list.get(1).getAmount());
        assertEquals(0.08, list.get(1).getFee());
        assertEquals(1, list.get(1).getId());
    }

    private UserDb user() {
        UserDb userDb = new UserDb();
        userDb.setId(1);
        userDb.setUsername("test");
        userDb.setEmail("test@test.com");
        userDb.setPassword("password");
        userDb.setSolde(3);
        userDb.setRole("USER");

        return userDb;
    }

    private CreatedUserDto createdUserDto() {
        CreatedUserDto userDto = new CreatedUserDto();
        userDto.setUsername("test");
        userDto.setEmail("test@test.com");
        userDto.setPassword("password");

        return userDto;
    }

}
