package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.entities.EntityMaterial;
import com.LeBonMeuble.backend.services.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MaterielController {

    private final MaterialService materialService;

    @GetMapping("/material")
    public ResponseEntity<List<EntityMaterial>> getMaterials() {
        List<EntityMaterial> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }
}