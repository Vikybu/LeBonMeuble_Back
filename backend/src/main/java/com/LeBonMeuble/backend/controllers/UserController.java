package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.DTO.UpdateUserDTO;
import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.repositories.UserRepository;
import com.LeBonMeuble.backend.services.CustomUserDetailsService;
import com.LeBonMeuble.backend.services.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final ServiceUser serviceUser;
    private final UserRepository userRepository;

    public UserController(PasswordEncoder passwordEncoder, ServiceUser serviceUser, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.serviceUser = serviceUser;
        this.userRepository = userRepository;
    }

    // 🔹 Création d’un utilisateur
    @PostMapping("/create")
    public ResponseEntity<String> addUser(@RequestBody EntityUser user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            serviceUser.createUser(user);

            return ResponseEntity.ok("User added successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Récupérer les infos de l'utilisateur
    @GetMapping("/user/infos/{id}")
    public ResponseEntity<?> getInfosUser(@PathVariable Long id) {
        try {
            EntityUser user = serviceUser.getInfosUserById(id);

            // On retire le mot de passe
            user.setPassword(null);

            return ResponseEntity.ok(user);

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Utilisateur introuvable");
        }
    }

    // 🔹 Update de l’utilisateur
    @PutMapping("/user/profile/modify/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto
    ) {
        EntityUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        EntityUser updatedUser = userRepository.save(user);

        updatedUser.setPassword(null); // NE PAS renvoyer le hash

        return ResponseEntity.ok(updatedUser);
    }
}

