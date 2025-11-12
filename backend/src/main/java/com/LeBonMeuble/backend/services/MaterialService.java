package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityMaterial;
import com.LeBonMeuble.backend.repositories.MaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    public List<EntityMaterial> getAllMaterials() {
        return materialRepository.findAll();
    }
}