package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for user registration.
 * <p>
 * Handles displaying the registration form and processing the registration submission.
 */
@Controller
public class RegistrationController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the registration form to the user.
     *
     * @param model model to add the CreatedUserDto attribute for form binding
     * @return the registration view name
     */
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("CreatedUserDto", new CreatedUserDto());
        return "registration";
    }

    /**
     * Processes the registration form submission.
     * <p>
     * If validation errors are present, displays the form.
     * Otherwise, attempts to create a new user.
     * On success, redirects to the login page.
     * On failure, adds an error message and displays the form.
     *
     * @param userDto the user data transfer object submitted by the form
     * @param result  binding result for validation errors
     * @param model   model to add error attributes if needed
     * @return the next view name, either "registration" or a redirect to login
     */
    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute("CreatedUserDto") @Valid CreatedUserDto userDto,
                                      BindingResult result,
                                      Model model) {
        if (result.hasErrors()) {
            return "registration";
        }

        try {
            userService.createUser(userDto);
            logger.info("User successfully created, redirection...");
            return "redirect:/login";
        } catch (RequestException e) {
            logger.info("Unable to created user, redirection...");
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }

}
