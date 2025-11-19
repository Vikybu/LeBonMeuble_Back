package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<EntityOrderItem, Long> { }
