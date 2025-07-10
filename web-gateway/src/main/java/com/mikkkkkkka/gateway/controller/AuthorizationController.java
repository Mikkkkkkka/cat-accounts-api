package com.mikkkkkkka.gateway.controller;

import com.mikkkkkkka.common.model.dto.UserDto;
import com.mikkkkkkka.gateway.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
public class AuthorizationController {

    private final UserDetailsServiceImpl userService;

    @Autowired
    public AuthorizationController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        UserDto user = userService.registerUser(userDto);
        return ResponseEntity.ok("User +\"" + user.username() + "\" registered successfully.");
    }
}
