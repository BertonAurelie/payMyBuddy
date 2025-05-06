package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<String> postUser(@RequestBody CreatedUserDto userDto) {
        int result = userService.createUser(userDto);
        if (result == 1) {
            return new ResponseEntity<>("User successfully created", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not created", HttpStatus.NOT_FOUND);
    }
}
