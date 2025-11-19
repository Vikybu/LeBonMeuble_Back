package com.LeBonMeuble.backend.repositories;

import com.LeBonMeuble.backend.entities.EntityOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<EntityOrder, Long> {

}
