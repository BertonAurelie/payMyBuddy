package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.CreatedUserMapper;
import com.aboc.payMyBuddy.model.dto.mapper.UpdatedUserMapper;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
        } else if (userRepository.findUserByUserName(userDto.getUsername()) != 0) {
            throw new RequestException("username already existing");
        }

        logger.info("creating user...");
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        UserDb userDb = CreatedUserMapper.toEntity(userDto);
        int result = userRepository.createUser(userDb.getUsername(), userDb.getEmail(), hashedPassword);

        return result;
    }


    public int updateUser(UpdatedUserDto userDto) {
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(currentPrincipalId);

        if (!(userDto.getUsername().equalsIgnoreCase(userDb.getUsername()))) {
            if(userRepository.findUserByUserName(userDto.getUsername()) != 0){
                throw new RequestException("Username already used");
            }
        }
        if (!(userDto.getEmail().equalsIgnoreCase(userDb.getEmail()))) {
            if(userRepository.findUserDbByEmail(userDto.getEmail()) != 0){
                throw new RequestException("email already existing");
            }
        }

        if (userDto.getPassword() != null && !(userDto.getPassword().equals(""))) {
            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(hashedPassword);
        }

        UserDb user = UpdatedUserMapper.toEntity(userDto);
        user.setId(currentPrincipalId);
        int result = userRepository.updateUser(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getSolde());
        return result;
    }

    public void addFriend(UpdatedUserDto friend){
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        UserDb userDb = userRepository.findUserById(currentPrincipalId);

        UserDb friendDb = userRepository.findUserByEmail(friend.getEmail());

        if (friendDb != null && !userDb.getFriends().contains(friendDb)) {
            userDb.getFriends().add(friendDb);

            userRepository.save(userDb);
        }
    }
}
