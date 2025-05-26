package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.mapper.UpdatedUserMapper;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.UserRepository;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfilController {
    private final UserService userService;
    private final UserRepository userRepository;

    public ProfilController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profil")
    public String getProfilPage(Model model) {
        //Retrieves authenticate user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        int currentPrincipalId = customUserDetails.getId();

        UserDb user = userRepository.findById(currentPrincipalId)
                .orElseThrow(() -> new RequestException("User not found"));

        UpdatedUserDto userDto = UpdatedUserMapper.toDto(user);
        System.out.println("loading profil page");

        model.addAttribute("UpdatedUserDto", userDto);
        return "profil";
    }

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
            System.out.println("modification utilisateur OK, redirection...");
            return "redirect:/transfert"; // ou page de succès
        } catch (RequestException e) {
            model.addAttribute("error", e.getMessage());
            return "profil";
        }
    }

}
