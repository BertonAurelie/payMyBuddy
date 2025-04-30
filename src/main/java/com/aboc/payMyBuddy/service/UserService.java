package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.model.User;
import com.aboc.payMyBuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * retrieves all users from DB
     * @return a list of {@link User}
     */
    public Iterable<User> getUsers(){
        return userRepository.findAll();
    }

    /**
     * Find user by id from DB
     * @param id
     * @return Optional<User>
     */
    public Optional<User> getUserById(int id){
        return userRepository.findById(id);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }
}
