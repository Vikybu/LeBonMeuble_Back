package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.exceptions.EmailAlreadyUsedException;
import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
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
}
