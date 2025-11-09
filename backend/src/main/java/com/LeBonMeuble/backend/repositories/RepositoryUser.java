package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.models.ModelUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryUser extends CrudRepository <ModelUser, Integer>{
    Optional<ModelUser> findByEmail(String email);
}
