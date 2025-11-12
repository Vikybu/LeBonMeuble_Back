package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.services.ServiceUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final ServiceUser serviceUser;

    public UserController(PasswordEncoder passwordEncoder, ServiceUser serviceUser) {
        this.passwordEncoder = passwordEncoder;
        this.serviceUser = serviceUser;
    }

    @PostMapping("/creationUser")
    public ResponseEntity<String> addUser(@RequestBody EntityUser user) {
        try {
            // Hash du mot de passe
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            serviceUser.createUser(user);

            return ResponseEntity.ok("User added successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
