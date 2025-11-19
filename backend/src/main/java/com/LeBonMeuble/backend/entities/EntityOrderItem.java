package com.LeBonMeuble.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "furniture_id")
    private EntityFurniture furniture;

    private String name;

    private Long price;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private EntityOrder order;
}