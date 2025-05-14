package com.aboc.payMyBuddy.controller;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/create")
    public ResponseEntity<String> postUser(@RequestBody @Valid CreatedUserDto userDto) throws Exception {
        int result = userService.createUser(userDto);
        if (result == 1) {
            return new ResponseEntity<>("User successfully created", HttpStatus.OK);
        }

        throw new RequestException("username already existing");
    }

    @GetMapping("/user")
    public String getUser() {
        return "Welcome, User";
    }

    @PutMapping("/users/update")
    public ResponseEntity<String> putUser(@RequestBody @Valid UserDb user) throws Exception {
        int result = userService.updateUser(user);

        if (result == 1) {
            return new ResponseEntity<>("User successfully updated", HttpStatus.OK);
        }

        throw new RequestException("updating failed");

    }

    @GetMapping("/debug-auth")
    public ResponseEntity<String> debugAuth() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok("Principal: " + principal.getClass().getName());
    }
}
