package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RelationController {
    private final UserService userService;

    public RelationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/relationPage")
    public String getRelationPage() {
        System.out.println("Accès à relationPage");
        return "relationPage";
    }

    @PostMapping("relation")
    public String processAddFriend(@ModelAttribute("UpdatedUserDto") @Valid UpdatedUserDto userDto,
                                   BindingResult result,
                                   Model model) {
        try {
            userService.addFriend(userDto);
            System.out.println("Ajout de la relation, redirection...");
            return "redirect:/transfert";
        } catch (Exception e) {
            System.out.println("échec de l'ajout de relation");
            return "relation";
        }

        //TODO:: Ajout gestion d'erreur
    }
}
