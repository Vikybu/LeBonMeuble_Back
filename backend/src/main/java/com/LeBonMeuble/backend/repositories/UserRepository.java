package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository <EntityUser, Long>{
    EntityUser findByEmail(String email);
}
