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

@Controller
public class RegistrationController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    //Affichage du formulaire
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("CreatedUserDto", new CreatedUserDto());
        return "registration";
    }

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
