package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FurnitureRepository extends JpaRepository<EntityFurniture, Long> {

    List<EntityFurniture> findByStatus(String status);

    List<EntityFurniture> findByUserId(Long user_id);

    Optional<EntityFurniture> findById(Long id);

    @Query("SELECT f FROM EntityFurniture f WHERE f.status = :status " +
            "AND (:materialId IS NULL OR f.material.id = :materialId) " +
            "AND (:colorId IS NULL OR f.color.id = :colorId) " +
            "AND (:typeId IS NULL OR f.type.id = :typeId)")
    List<EntityFurniture> findByStatusWithFilters(
            @Param("status") String status,
            @Param("materialId") Long materialId,
            @Param("colorId") Long colorId,
            @Param("typeId") Long typeId
    );
}