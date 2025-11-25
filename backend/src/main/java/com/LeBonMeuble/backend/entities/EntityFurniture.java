package com.LeBonMeuble.backend.entities;

import com.LeBonMeuble.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(Views.FurnitureOutput.class)
    private Long id;

    @JsonView(Views.FurnitureOutput.class)
    private String name;

    @JsonView(Views.FurnitureOutput.class)
    private String description;

    @JsonView(Views.FurnitureOutput.class)
    private String price;

    @JsonView(Views.FurnitureOutput.class)
    private String status;

    @JsonView(Views.FurnitureOutput.class)
    private String width;

    @JsonView(Views.FurnitureOutput.class)
    private String height;

    @JsonView(Views.FurnitureOutput.class)
    private String length;

    // 🧩 Relations (clés étrangères)
    @ManyToOne
    @JsonView(Views.FurnitureOutput.class)
    @JoinColumn(name = "type_id")
    private EntityType type;

    @ManyToOne
    @JsonView(Views.FurnitureOutput.class)
    @JoinColumn(name = "color_id")
    private EntityColor color;

    @ManyToOne
    @JsonView(Views.FurnitureOutput.class)
    @JoinColumn(name = "material_id")
    private EntityMaterial material;

    @ManyToOne
    @JsonView(Views.FurnitureOutput.class)
    @JoinColumn(name = "user_id")
    private EntityUser user;

    @ManyToOne
    @JsonView(Views.FurnitureOutput.class)
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
