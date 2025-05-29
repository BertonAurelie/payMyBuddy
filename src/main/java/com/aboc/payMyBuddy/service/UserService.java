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
import java.util.Optional;

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
     * retrieves all users from DB
     *
     * @return a list of {@link UserDb}
     */
    public Iterable<UserDb> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Find user by id from DB
     *
     * @param id
     * @return Optional<User>
     */
    public Optional<UserDb> getUserById(int id) {
        return userRepository.findById(id);
    }

    /**
     * Method to create user with hashing password and using CreatedUserDto
     *
     * @param userDto
     * @return 1 if user created.
     */
    public int createUser(CreatedUserDto userDto) {

        if (userRepository.findUserDbByEmail(userDto.getEmail()) != 0) {
            throw new RequestException("email already existing");
        } else if (userRepository.findUserDbByUserName(userDto.getUsername()) != 0) {
            throw new RequestException("username already existing");
        }

        logger.info("creating user...");
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        UserDb userDb = CreatedUserMapper.toEntity(userDto);
        int result = userRepository.createUser(userDb.getUsername(), userDb.getEmail(), hashedPassword);

        return result;
    }


    public int updateUser(UpdatedUserDto userDto) {
        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(getAuthId());

        if (!(userDto.getUsername().equalsIgnoreCase(userDb.getUsername()))) {
            if (userRepository.findUserDbByUserName(userDto.getUsername()) != 0) {
                throw new RequestException("Username already used");
            }
        }
        if (!(userDto.getEmail().equalsIgnoreCase(userDb.getEmail()))) {
            if (userRepository.findUserDbByEmail(userDto.getEmail()) != 0) {
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

    public int moneyDeposit(UpdatedUserDto userDto, int valueAction) {
        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(getAuthId());

        double solde = userDb.getSolde();
        if (userDto.getSolde() != 0 && valueAction == 0) {
            solde += userDto.getSolde();
            solde = BigDecimal.valueOf(solde).setScale(2, RoundingMode.FLOOR).doubleValue();
        }

        if (userDto.getSolde() != 0 && valueAction == 1) {
            if (userDb.getSolde() > userDto.getSolde()) {
                solde -= userDto.getSolde();
                solde = BigDecimal.valueOf(solde).setScale(2, RoundingMode.FLOOR).doubleValue();
            } else {
                throw new RequestException("insufficient funds");
            }
        }


        int result = userRepository.updateUser(userDb.getId(), userDb.getUsername(), userDb.getEmail(), userDb.getPassword(), solde);
        return result;
    }

    public void addFriend(UpdatedUserDto friend) {
        UserDb userDb = userRepository.findUserById(getAuthId());
        UserDb friendDb = userRepository.findUserByEmail(friend.getEmail());

        if (userRepository.findUserDbByEmail(friend.getEmail()) == 0) {
            throw new RequestException("unknown user");
        }

        if (friendDb != null && !userDb.getFriends().contains(friendDb)) {
            userDb.getFriends().add(friendDb);

            userRepository.save(userDb);
        } else {
            throw new RequestException("already a friend ");
        }
    }

    public List<String> showListFriends() {
        UserDb userDb = userRepository.findUserById(getAuthId());

        List<String> listFriends = new ArrayList<>();

        for (UserDb friend : userDb.getFriends()) {
            listFriends.add(friend.getUsername());
        }

        return listFriends;
    }

    public void sendMoney(TransactionDto transactiondto) {
        UserDb sender = userRepository.findUserById(getAuthId());

        UserDb receiver = userRepository.findUserByUsername(transactiondto.getReceiver());

        if (receiver != null && transactiondto.getDescription() != null && transactiondto.getAmount() > 0) {
            double fee = BigDecimal.valueOf(0.5 / 100 * transactiondto.getAmount()).setScale(2, RoundingMode.FLOOR).doubleValue();

            if (sender.getSolde() > transactiondto.getAmount() + fee) {
                Transaction transactionSave = new Transaction(sender, receiver, transactiondto.getDescription(), transactiondto.getAmount(), fee);
                transactionRepository.save(transactionSave);

                receiver.setSolde(BigDecimal.valueOf(receiver.getSolde() + transactiondto.getAmount()).setScale(2, RoundingMode.FLOOR).doubleValue());
                userRepository.save(receiver);

                sender.setSolde(BigDecimal.valueOf(sender.getSolde() - (transactiondto.getAmount() + fee)).setScale(2, RoundingMode.FLOOR).doubleValue());
                userRepository.save(sender);

            } else {
                throw new RequestException("insufficient funds");
            }
        }

    }

    public List<Transaction> showTransaction() {
        List<Transaction> transactionList = transactionRepository.findTransactionBySenderId(getAuthId());

        return transactionList;
    }

    private int getAuthId() {
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        return currentPrincipalId;
    }
}
