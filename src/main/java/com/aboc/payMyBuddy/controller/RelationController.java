package com.aboc.payMyBuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RelationController {

    @GetMapping("/relationPage")
    public String getRelationPage() {
        System.out.println("Accès à relationPage");
        return "relationPage";
    }
}
