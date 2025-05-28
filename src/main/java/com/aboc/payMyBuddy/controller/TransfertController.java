package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.Transaction;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.model.dto.request.TransactionDto;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TransfertController {
    private UserService userService;

    public TransfertController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/transfert")
    public String getTransfertPage(Model model){

        model.addAttribute("TransactionDto", new TransactionDto());
        model.addAttribute("options", userService.showListFriends());
        model.addAttribute("transaction", userService.showTransaction());

        return "transfert";
    }

    @PostMapping("/transfert")
    public String processRegistration(@ModelAttribute("TransactionDto") @Valid TransactionDto transaction,
                                      BindingResult result,
                                      Model model) {
        if (result.hasErrors()) {
            return "transfert";
        }

        try {
            userService.sendMoney(transaction);
            System.out.println("transfert OK, redirection...");
            return "redirect:/transfert";
        } catch (RequestException e) {
            model.addAttribute("error", e.getMessage());
            return "transfert";
        }
    }
}
