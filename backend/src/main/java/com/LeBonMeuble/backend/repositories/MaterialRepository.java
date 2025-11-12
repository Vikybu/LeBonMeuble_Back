package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<EntityMaterial, Long> {
    EntityMaterial findByName(String name);
}
