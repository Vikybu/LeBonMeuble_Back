package com.LeBonMeuble.backend.entities;

import com.LeBonMeuble.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "material")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.FurnitureOutput.class)
    private String name;
}