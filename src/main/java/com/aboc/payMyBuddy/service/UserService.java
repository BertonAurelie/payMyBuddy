package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.exception.ErrorEntity;
import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.User;
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
     * @return a list of {@link User}
     */
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Find user by id from DB
     *
     * @param id
     * @return Optional<User>
     */
    public Optional<User> getUserById(int id) {
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
        User user = CreatedUserMapper.toEntity(userDto);
        int result = userRepository.createUser(user.getUsername(), user.getEmail(), hashedPassword);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object currentPrincipalName = authentication.getPrincipal();
        return result;
    }

}
