package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.TransactionDto;
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
public class TransfertController {
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TransfertController.class);

    public TransfertController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/transfert")
    public String getTransfertPage(Model model) {
        model.addAttribute("TransactionDto", new TransactionDto());
        model.addAttribute("options", userService.showListFriends());
        model.addAttribute("transaction", userService.showTransaction());

        return "transfert";
    }

    @PostMapping("/transfert")
    public String processRegistration(@ModelAttribute("TransactionDto") @Valid TransactionDto transaction,
                                      BindingResult result,
                                      Model model) {

        model.addAttribute("options", userService.showListFriends());
        model.addAttribute("transaction", userService.showTransaction());

        if (result.hasErrors()) {
            return "transfert";
        }

        try {
            userService.sendMoney(transaction);
            logger.info("Money transferred successfully, redirection...");
            return "redirect:/transfert";
        } catch (RequestException e) {
            logger.info("Money transfer failed, redirection...");
            model.addAttribute("error", e.getMessage());
            return "transfert";
        }
    }
}
