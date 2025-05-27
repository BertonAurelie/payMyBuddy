package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        System.out.println("Accès à transfertPage");

        List<String> options = userService.showListFriends();

        model.addAttribute("options", options);

        return "transfert";
    }


}
