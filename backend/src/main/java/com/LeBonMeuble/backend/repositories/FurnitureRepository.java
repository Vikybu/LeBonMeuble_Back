package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FurnitureRepository extends JpaRepository<EntityFurniture, Long> {
    List<EntityFurniture> findByStatus(String status);

    List<EntityFurniture> findByUserId(Long user_id);

}
