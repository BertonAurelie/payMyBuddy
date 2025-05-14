package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.CreatedUserMapper;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public int createUser(CreatedUserDto userDto) throws Exception {

        if (userRepository.findUserByEmail(userDto.getEmail()) != 0){
            throw new RequestException("email already existing");
        } else if (userRepository.findUserByUserName(userDto.getUsername()) != 0) {
            throw new RequestException("username already existing");
        }

        logger.info("creating user...");
        String hashedPassword = passwordEncoder.encode(userDto.getPassword());
        UserDb userDb = CreatedUserMapper.toEntity(userDto);
        int result = userRepository.createUser(userDb.getUsername(), userDb.getEmail(), hashedPassword);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object currentPrincipalName = authentication.getPrincipal();
        return result;
    }

    public int updateUser(UserDb user){
        //Retrieves authenticate user
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        //Check if it's the same ID
        if(!(currentPrincipalId == user.getId())){
            throw new RequestException("You are not allowed to update this user.");
        }

        if(user.getUsername() != null && userRepository.findUserByUserName(user.getUsername()) != 0){
            throw  new RequestException("Username already used");
        }
        if(user.getEmail() != null && userRepository.findUserByEmail(user.getEmail()) != 0){
            throw new RequestException("email already existing");
        }

        if (user.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        }

        int result = userRepository.updateUser(currentPrincipalId, user.getUsername(), user.getEmail(), user.getPassword(), user.getSolde());
        return result;
    }
}
