package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.Transaction;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.CreatedUserMapper;
import com.aboc.payMyBuddy.model.dto.mapper.UpdatedUserMapper;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.model.dto.request.TransactionDto;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.TransactionRepository;
import com.aboc.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, TransactionRepository transactionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method to create user with hashing password and using CreatedUserDto
     *
     * @param userDto
     * @return 1 if user created.
     */
    public int createUser(CreatedUserDto userDto) {

        if (getUserByEmail(userDto.getEmail()) != null) {
            throw new RequestException("email already existing");
        } else if (getUserByUsername(userDto.getUsername()) != null) {
            throw new RequestException("username already existing");
        }

        logger.info("creating user...");
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        UserDb userDb = CreatedUserMapper.toEntity(userDto);
        int result = userRepository.createUser(userDb.getUsername(), userDb.getEmail(), hashedPassword);

        return result;
    }

    /**
     * Method to update user with hashing password and using UpdatedUserDto
     *
     * @param userDto
     * @return 1 if user updated.
     */
    public int updateUser(UpdatedUserDto userDto) {
        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(getAuthId());

        if (!(userDto.getUsername().equalsIgnoreCase(userDb.getUsername()))) {
            if (getUserByUsername(userDto.getUsername()) != null) {
                throw new RequestException("Username already used");
            }
        }

        if (!(userDto.getEmail().equalsIgnoreCase(userDb.getEmail()))) {
            if (getUserByEmail(userDto.getEmail()) != null) {
                throw new RequestException("email already existing");
            }
        }

        if (userDto.getPassword() != null && !(userDto.getPassword().equals(""))) {
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(hashedPassword);
        }

        UserDb user = UpdatedUserMapper.toEntity(userDto);
        user.setId(userDb.getId());
        int result = userRepository.updateUser(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getSolde());
        return result;
    }

    /**
     * method to deposit money into the account or transfer money to bank account. Using UpdatedUserDto
     *
     * @param userDto and valueAction to know if we deposit or transfer money
     * @return 1 if transfer successfully.
     */
    public int moneyDeposit(UpdatedUserDto userDto, int valueAction) {
        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(getAuthId());

        double solde = userDb.getSolde();
        if (userDto.getSolde() != 0 && valueAction == 0) {
            solde += userDto.getSolde();
            solde = roundToTwoDecimals(solde);
        }

        if (userDto.getSolde() != 0 && valueAction == 1) {
            if (userDb.getSolde() >= userDto.getSolde()) {
                solde -= userDto.getSolde();
                solde = roundToTwoDecimals(solde);
            } else {
                throw new RequestException("insufficient funds");
            }
        }

        int result = userRepository.updateUser(userDb.getId(), userDb.getUsername(), userDb.getEmail(), userDb.getPassword(), solde);
        return result;
    }

    /**
     * method to add friend. Using UpdatedUserDto
     *
     * @param friend
     * @return 1 if friend added successfully or 0 if friend added fail.
     */
    public void addFriend(UpdatedUserDto friend) {
        if (getUserByEmail(friend.getEmail()) == null) {
            throw new RequestException("unknown user");
        }

        UserDb userDb = userRepository.findUserById(getAuthId());
        UserDb friendDb = getUserByEmail(friend.getEmail());

        if (friendDb != null && !userDb.getFriends().contains(friendDb)) {
            userDb.getFriends().add(friendDb);

            userRepository.save(userDb);
        } else {
            throw new RequestException("already a friend");
        }
    }

    /**
     * Retrieves list of friends.
     *
     * @return list of string(username's friend).
     */
    public List<String> showListFriends() {
        UserDb userDb = userRepository.findUserById(getAuthId());

        List<String> listFriends = new ArrayList<>();

        for (UserDb friend : userDb.getFriends()) {
            listFriends.add(friend.getUsername());
        }

        return listFriends;
    }

    /**
     * method to send money at friend. Using TransactionDto
     *
     * @param transactiondto
     */
    public void sendMoney(TransactionDto transactiondto) {
        UserDb sender = userRepository.findUserById(getAuthId());

        UserDb receiver = getUserByUsername(transactiondto.getReceiver());

        if (receiver != null && transactiondto.getDescription() != null && transactiondto.getAmount() > 0) {
            double fee = roundToTwoDecimals(0.5 / 100 * transactiondto.getAmount());

            if (sender.getSolde() > transactiondto.getAmount() + fee) {
                Transaction transactionSave = new Transaction(sender, receiver, transactiondto.getDescription(), transactiondto.getAmount(), fee);
                transactionRepository.save(transactionSave);

                receiver.setSolde(roundToTwoDecimals(receiver.getSolde() + transactiondto.getAmount()));
                userRepository.save(receiver);

                sender.setSolde(roundToTwoDecimals(sender.getSolde() - (transactiondto.getAmount() + fee)));
                userRepository.save(sender);

            } else {
                throw new RequestException("insufficient funds");
            }
        }

    }

    /**
     * Retrieves list of user's transactions
     *
     * @return List of transaction
     */
    public List<Transaction> showTransaction() {
        List<Transaction> transactionList = transactionRepository.findTransactionBySenderId(getAuthId());

        return transactionList;
    }

    /**
     * Private method to retrieve logged user's data
     *
     * @return user's id
     */
    private int getAuthId() {
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        return currentPrincipalId;
    }

    /**
     * private method to found user on database with email param
     *
     * @param email
     * @return UserDb found with email
     */
    private UserDb getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /**
     * private method to found user on database with username param
     *
     * @param username
     * @return UserDb found with username
     */
    private UserDb getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Rounds a double value to two decimal places using floor rounding mode.
     *
     * @param value the value to round
     * @return the rounded value with two decimals
     */
    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.FLOOR).doubleValue();
    }

}
