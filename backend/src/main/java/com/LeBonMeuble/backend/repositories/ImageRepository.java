package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<EntityImage, Long> {}
