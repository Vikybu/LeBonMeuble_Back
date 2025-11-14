package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import com.LeBonMeuble.backend.repositories.FurnitureRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FurnitureService {

    private final FurnitureRepository furnitureRepository;

    public FurnitureService(FurnitureRepository furnitureRepository) {
        this.furnitureRepository = furnitureRepository;
    }

    public List<EntityFurniture> getInfosFurniture() {
        return furnitureRepository.findAll();
    }

    public List<EntityFurniture> getFurnitureByStatus(String status) {
        return furnitureRepository.findByStatus(status);
    }

    public void updateStatus(Long id, String status) {
        EntityFurniture furniture = furnitureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        furniture.setStatus(status);
        furnitureRepository.save(furniture);
    }
}
