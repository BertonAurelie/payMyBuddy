package com.aboc.payMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransfertController {

    @GetMapping("/transfert")
    public String getTransfertPage(){
        System.out.println("Accès à transfertPage");
        return "transfert";
    }
}
