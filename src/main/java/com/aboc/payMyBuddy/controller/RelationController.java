package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
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
public class RelationController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RelationController.class);

    public RelationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/relationPage")
    public String getRelationPage(Model model) {
        model.addAttribute("UpdatedUserDto", new UpdatedUserDto());
        return "relationPage";
    }

    @PostMapping("relation")
    public String processAddFriend(@ModelAttribute("UpdatedUserDto") @Valid UpdatedUserDto userDto,
                                   BindingResult result,
                                   Model model) {

        if (result.hasErrors()) {
            return "relationPage";
        }

        try {
            userService.addFriend(userDto);
            logger.info("friend successfully added, redirection...");
            return "redirect:/transfert";
        } catch (RequestException e) {
            logger.info("Unable to add this friend, redirection...");
            model.addAttribute("error", e.getMessage());
            return "relationPage";
        }

    }
}
