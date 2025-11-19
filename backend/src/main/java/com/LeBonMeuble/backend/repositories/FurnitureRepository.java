package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FurnitureRepository extends JpaRepository<EntityFurniture, Long> {
    List<EntityFurniture> findByStatus(String status);

    List<EntityFurniture> findByUserId(Long user_id);

    Optional<EntityFurniture> findById(Long id);
}

