package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.entities.*;
import com.LeBonMeuble.backend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/addFurniture")
public class FurnitureController {

    private final FurnitureRepository furnitureRepository;
    private final ImageRepository imageRepository;
    private final TypeRepository typeRepository;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createFurniture(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price,
            @RequestParam("status") String status,
            @RequestParam(value = "width", required = false) Double width,
            @RequestParam(value = "height", required = false) Double height,
            @RequestParam(value = "length", required = false) Double length,
            @RequestParam("user_id") Long userId,
            @RequestParam("type_id") Long typeId,
            @RequestParam("material_id") Long materialId,
            @RequestParam("color_id") Long colorId,
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            // Vérification des entités liées
            EntityType type = typeRepository.findById(typeId).orElse(null);
            EntityColor color = colorRepository.findById(colorId).orElse(null);
            EntityMaterial material = materialRepository.findById(materialId).orElse(null);
            EntityUser user = userRepository.findById(userId).orElse(null);

            if (type == null || color == null || material == null || user == null) {
                return ResponseEntity.badRequest().body("Une clé étrangère est invalide !");
            }

            // Enregistrement de l'image
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Sauvegarde de l'image en base
            EntityImage imageEntity = new EntityImage();
            imageEntity.setImage_url("/uploads/" + fileName);
            imageEntity.setAlt_text("Image du meuble " + name);
            imageRepository.save(imageEntity);

            // Création du meuble
            EntityFurniture furniture = new EntityFurniture();
            furniture.setName(name);
            furniture.setDescription(description);
            furniture.setPrice(price);
            furniture.setStatus(status);
            furniture.setWidth(width != null ? width.toString() : null);
            furniture.setHeight(height != null ? height.toString() : null);
            furniture.setLength(length != null ? length.toString() : null);
            furniture.setUser(user);
            furniture.setType(type);
            furniture.setMaterial(material);
            furniture.setColor(color);
            furniture.setImage(imageEntity);

            furnitureRepository.save(furniture);

            return ResponseEntity.ok("✅ Meuble enregistré avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement de l'image : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur inattendue : " + e.getMessage());
        }
    }
}