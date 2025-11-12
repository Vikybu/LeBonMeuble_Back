package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeRepository extends JpaRepository<EntityType, Long> {
    EntityType findByName(String name);
}