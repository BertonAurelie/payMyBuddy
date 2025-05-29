package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.MyEnumType;
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
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DepositController {
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DepositController.class);

    public DepositController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/deposit")
    public String getDepositPage(Model model) {
        model.addAttribute("UpdatedUserDto", new UpdatedUserDto());
        return "deposit";
    }

    @PostMapping("deposit")
    public String processAddFriend(@ModelAttribute("UpdatedUserDto") @Valid UpdatedUserDto userDto, @RequestParam(value = "action", required = true) String action,
                                   BindingResult result,
                                   Model model) {

        if (result.hasErrors()) {
            return "deposit";
        }

        try {
            int valueAction = 0;

            if (action.equals(MyEnumType.RETRAIT)) {
                valueAction = 1;
            }

            userService.moneyDeposit(userDto, valueAction);
            logger.info("Deposit money, redirection...");
            return "redirect:/transfert";
        } catch (RequestException e) {
            logger.info("Money deposit failed");
            model.addAttribute("error", e.getMessage());
            return "deposit";
        }

    }

}
