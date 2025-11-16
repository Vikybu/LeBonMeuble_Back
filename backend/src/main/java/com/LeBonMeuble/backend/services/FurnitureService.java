package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityFurniture;
import com.LeBonMeuble.backend.entities.EntityImage;
import com.LeBonMeuble.backend.repositories.*;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    // Mise à jour du statut
    public void updateStatus(Long id, String status) {
        EntityFurniture furniture = furnitureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));

        furniture.setStatus(status);   // ← ⚡️ ICI on met le nouveau status
        furnitureRepository.save(furniture);
    }

    // Trouver les meubles d’un user
    public List<EntityFurniture> getFurnitureByUser(Long userId) {
        return furnitureRepository.findByUserId(userId);
    }

    // Meuble par ID
    public EntityFurniture findById(Long id) {
        return furnitureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Furniture not found"));
    }

    //Suppression d'un meuble
    public void deleteFurniture(Long id){
        furnitureRepository.deleteById(id);
    }

    //Modifier un meuble
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ImageRepository imageRepository;

    public EntityFurniture updateFurniture(
            Long id,
            String name,
            String description,
            String price,
            String status,
            String width,
            String height,
            String length,
            Long typeId,
            Long colorId,
            Long materialId,
            MultipartFile imageFile
    ) throws IOException {

        EntityFurniture furniture = furnitureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meuble introuvable"));

        // 🔄 Mise à jour des champs simples
        furniture.setName(name);
        furniture.setDescription(description);
        furniture.setPrice(price);
        furniture.setStatus(status);
        furniture.setWidth(width);
        furniture.setHeight(height);
        furniture.setLength(length);

        // 🔄 Mise à jour des relations
        furniture.setType(typeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Type invalide")));

        furniture.setColor(colorRepository.findById(colorId)
                .orElseThrow(() -> new RuntimeException("Couleur invalide")));

        furniture.setMaterial(materialRepository.findById(materialId)
                .orElseThrow(() -> new RuntimeException("Matériau invalide")));

        // 🔄 Si une nouvelle image est envoyée
        if (imageFile != null && !imageFile.isEmpty()) {
            EntityImage newImage = saveImage(imageFile);
            furniture.setImage(newImage);
        }

        return furnitureRepository.save(furniture);
    }


    // 👉 Méthode utilitaire de sauvegarde d'image
    private EntityImage saveImage(MultipartFile file) throws IOException {
        EntityImage img = new EntityImage();
        img.setAlt_text("image meuble");
        img.setImage_url("/images/" + file.getOriginalFilename());
        return imageRepository.save(img);
    }
}

