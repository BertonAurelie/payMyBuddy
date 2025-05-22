package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

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
            System.out.println("Création utilisateur OK, redirection...");
            return "redirect:/login"; // ou page de succès
        } catch (RequestException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }

}
