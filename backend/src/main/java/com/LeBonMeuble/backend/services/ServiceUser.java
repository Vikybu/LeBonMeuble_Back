package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.exceptions.EmailAlreadyUsedException;
import com.LeBonMeuble.backend.models.ModelUser;
import com.LeBonMeuble.backend.repositories.RepositoryUser;
import org.springframework.stereotype.Service;

@Service
public class ServiceUser {

    private final RepositoryUser repositoryUser;

    public ServiceUser(RepositoryUser repositoryUser) {
        this.repositoryUser = repositoryUser;
    }

    public ModelUser createUser(ModelUser user) {
        if (repositoryUser.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Cet email est déjà utilisé.");
        }
        return repositoryUser.save(user);
    }
}
