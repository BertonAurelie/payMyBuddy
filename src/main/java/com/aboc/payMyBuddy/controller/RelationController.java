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

/**
 * Controller  for handling friend relationship management.
 * <p>
 * Provides endpoints for:
 * - Displaying the page to add a new friend
 * - Processing the friend addition form
 * Need to be authenticated to access this page.
 */
@Controller
public class RelationController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RelationController.class);

    public RelationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Displays the page to add a new friend.
     *
     * @param model the model to pass attributes to the view
     * @return the name of the view displaying the friend relation form
     */
    @GetMapping("/relationPage")
    public String getRelationPage(Model model) {
        model.addAttribute("UpdatedUserDto", new UpdatedUserDto());
        return "relationPage";
    }


    /**
     * Handles the form submission for adding a new friend.
     * <p>
     * If the submitted form has validation errors, it displays the form with the errors.
     * If the friend addition is successful, it redirects to the transfer page.
     * If the service throws a RequestException, the error message is added to the model and the form is redisplayed.
     *
     * @param userDto the data transfer object containing friend information
     * @param result  the binding result for validation errors
     * @param model   the model to pass attributes to the view
     * @return the name of the view to render or a redirect URL
     */
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
