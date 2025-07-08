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

/**
 * Controller for handling transfert money to friend relationship
 * Provides endpoints for :
 * - displaying the page to transfert money
 * - processing transfert money
 * Need to be authenticated to acces this page
 */
@Controller
public class TransfertController {
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TransfertController.class);

    public TransfertController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the page to transfert money to a friend
     *
     * @param model the model to pass attributes to the view
     * @return the name of the view displaying the transfert money form
     */
    @GetMapping("/transfert")
    public String getTransfertPage(Model model) {
        model.addAttribute("TransactionDto", new TransactionDto());
        model.addAttribute("options", userService.showListFriends());
        model.addAttribute("transaction", userService.showTransaction());

        return "transfert";
    }

    /**
     * Handles the submission of a money transfer request.
     * <p>
     * This method validates the TransactionDto, and if valid,
     * attempts to process the money transfer using sendMoney to userService with TransactionDto param.
     * In case of errors during validation or processing, it returns the "transfert" page
     * with appropriate error messages.
     *
     * @param transaction the transaction data submitted by the user
     * @param result      the binding result that contains validation errors if any
     * @param model       the model to pass attributes to the view
     * @return the "transfert" page with success or error info, or a redirect on success
     */
    @PostMapping("/transfert")
    public String processTransfert(@ModelAttribute("TransactionDto") @Valid TransactionDto transaction,
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
