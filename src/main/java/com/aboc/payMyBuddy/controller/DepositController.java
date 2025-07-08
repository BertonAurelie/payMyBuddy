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

/**
 * Controller responsible for handling deposit operations.
 * Provides endpoints to display the deposit page and to process
 * deposit or withdrawal requests from users.
 * Requires user authentication.
 */
@Controller
public class DepositController {
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DepositController.class);

    public DepositController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the deposit page with a form to enter deposit or withdrawal data.
     *
     * @param model the model to add attributes to the view
     * @return the deposit page view name
     */
    @GetMapping("/deposit")
    public String getDepositPage(Model model) {
        model.addAttribute("UpdatedUserDto", new UpdatedUserDto());
        return "deposit";
    }

    /**
     * Processes the deposit or withdrawal request based on the submitted form data.
     *
     * @param userDto the data transfer object containing user information and amount
     * @param action  the action to perform, expected values: deposit or withdrawal (based on MyEnumType)
     * @param result  binding result to check for validation errors
     * @param model   the model to add attributes such as error messages
     * @return redirect to transfert page on success or deposit page on failure
     */
    @PostMapping("deposit")
    public String processAddMoney(@ModelAttribute("UpdatedUserDto") @Valid UpdatedUserDto userDto, @RequestParam(value = "action", required = true) String action,
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
