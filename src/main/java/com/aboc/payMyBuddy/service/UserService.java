package com.aboc.payMyBuddy.service;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.CreatedUserMapper;
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


    public int updateUser(UserDb user) {
        //Retrieves authenticate user
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        //Check if it's the same ID
        if (!(currentPrincipalId == user.getId())) {
            throw new RequestException("You are not allowed to update this user.");
        }

        if (user.getUsername() != null && userRepository.findUserByUserName(user.getUsername()) != 0) {
            throw new RequestException("Username already used");
        }
        if (user.getEmail() != null && userRepository.findUserDbByEmail(user.getEmail()) != 0) {
            throw new RequestException("email already existing");
        }

        if (user.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
        }

        int result = userRepository.updateUser(currentPrincipalId, user.getUsername(), user.getEmail(), user.getPassword(), user.getSolde());
        return result;
    }

    public void addFriend(UpdatedUserDto friend){
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        //On récupère les données de l'utilisateur connecté
        UserDb userDb = userRepository.findUserById(currentPrincipalId);

        // On cherche l'ami à ajouter via son email
        UserDb friendDb = userRepository.findUserByEmail(friend.getEmail());

        // Si l'ami existe et qu'il n'est pas déjà dans la liste
        if (friendDb != null && !userDb.getFriends().contains(friendDb)) {
            //On l'ajoute à la liste des amis de l'utilisateur connecté
            userDb.getFriends().add(friendDb);

            // On sauvegarde les nouvelles données dans la base de données
            userRepository.save(userDb);
        }
    }
}
