package com.aboc.payMyBuddy.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class to handle login and logout operations.
 */
@Controller
public class LoginController {

    /**
     * Displays the login page.If the error parameter is present, an error message is added to the model.
     *
     * @param error optional parameter indicating login failure
     * @param model the model used to pass attributes to the view
     * @return the name of the login view
     */
    @GetMapping("/login")
    public String getLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "invalid credentials");
        }
        return "login";
    }

    /**
     * Logs out the current user if authenticated and redirects to the login page with a logout parameter.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return a redirect string to the login page
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }
}
