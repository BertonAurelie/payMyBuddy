package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.UpdatedUserMapper;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.UserRepository;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller class to handle the user profil page.
 * Allows authenticated users to view and update their account information,
 * such as username, email, and password.
 */
@Controller
public class ProfilController {
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProfilController.class);

    public ProfilController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Displays the profile page with the current user's information pre-filled.
     * The user must be authenticated.
     *
     * @param model the model used to pass the user DTO to the view
     * @return the profile view name
     * @throws RequestException if the user is not found in the repository
     */
    @GetMapping("/profil")
    public String getProfilPage(Model model) {
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        UserDb user = userRepository.findById(currentPrincipalId)
                .orElseThrow(() -> new RequestException("User not found"));

        UpdatedUserDto userDto = UpdatedUserMapper.toDto(user);
        model.addAttribute("UpdatedUserDto", userDto);
        return "profil";
    }

    /**
     * Handles submission of the updated user profile form.
     * Validates the input and calls the service to perform the update.
     * If successful, redirects to the transfer page; otherwise, returns to the profile page
     * with validation errors or a service error.
     *
     * @param userDto
     * @param result
     * @param model
     * @return redirect to the transfer page on success, or the profile view with errors
     */
    @PostMapping("/profil")
    public String updateUser(@ModelAttribute("UpdatedUserDto") @Valid UpdatedUserDto userDto,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("UpdatedUserDto", userDto);
            return "profil";
        }

        try {
            userService.updateUser(userDto);
            logger.info("user successfully updated, redirection...");
            return "redirect:/transfert"; // ou page de succès
        } catch (RequestException e) {
            logger.info("Unable to update user, redirection...");
            model.addAttribute("error", e.getMessage());
            return "profil";
        }
    }

}
