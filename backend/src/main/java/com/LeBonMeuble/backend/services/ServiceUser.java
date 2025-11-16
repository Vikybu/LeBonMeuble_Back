package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.DTO.UpdateUserDTO;
import com.LeBonMeuble.backend.exceptions.EmailAlreadyUsedException;
import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ServiceUser {

    private final UserRepository repositoryUser;

    public ServiceUser(UserRepository repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    public EntityUser createUser(EntityUser user) {
        EntityUser existingUser = repositoryUser.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("Email déjà utilisé");
        }
        return repositoryUser.save(user);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public EntityUser updateUser(Long id, UpdateUserDTO dto) {

        EntityUser user = repositoryUser.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Mise à jour des champs simples
        user.setFirstname(dto.getFirstname());
        user.setLastname(dto.getLastname());
        user.setEmail(dto.getEmail());

        // Mise à jour du mot de passe uniquement si transmis
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return repositoryUser.save(user);
    }

    public EntityUser getInfosUserById(long id) {
        return repositoryUser.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
