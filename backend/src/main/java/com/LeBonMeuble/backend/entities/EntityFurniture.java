package com.LeBonMeuble.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "furniture")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityFurniture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String price;
    private String status;
    private String width;
    private String height;
    private String length;

    // 🧩 Relations (clés étrangères)
    @ManyToOne
    @JoinColumn(name = "type_id")
    private EntityType type;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private EntityColor color;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private EntityMaterial material;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private EntityUser user;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private EntityImage image;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        updated_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }
}
