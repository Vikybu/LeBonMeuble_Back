package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityColor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorRepository extends JpaRepository<EntityColor, Long> {
    EntityColor findByName(String name);
}
