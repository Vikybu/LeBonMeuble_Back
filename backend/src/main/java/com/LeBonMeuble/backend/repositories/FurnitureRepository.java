package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FurnitureRepository extends JpaRepository<EntityFurniture, Long> {}
