package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.DTO.UpdateStatusRequest;
import com.LeBonMeuble.backend.entities.*;
import com.LeBonMeuble.backend.repositories.*;
import com.LeBonMeuble.backend.services.FurnitureService;
import com.LeBonMeuble.backend.views.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FurnitureController {

    private final FurnitureService furnitureService;
    private final FurnitureRepository furnitureRepository;
    private final ImageRepository imageRepository;
    private final TypeRepository typeRepository;
    private final ColorRepository colorRepository;
    private final MaterialRepository materialRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads/";

    // -----------------------------
    //       CREATE FURNITURE
    // -----------------------------
    @PostMapping(path = "/furnitures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            // Vérification des relations
            EntityType type = typeRepository.findById(typeId).orElse(null);
            EntityColor color = colorRepository.findById(colorId).orElse(null);
            EntityMaterial material = materialRepository.findById(materialId).orElse(null);
            EntityUser user = userRepository.findById(userId).orElse(null);

            if (type == null || color == null || material == null || user == null) {
                return ResponseEntity.badRequest().body("Une clé étrangère est invalide !");
            }

            // Upload image
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Enregistrer image DB
            EntityImage img = new EntityImage();
            img.setImage_url("/uploads/" + fileName);
            img.setAlt_text("Image du meuble " + name);
            imageRepository.save(img);

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
            furniture.setImage(img);

            furnitureRepository.save(furniture);

            return ResponseEntity.ok("Meuble enregistré avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur d'image : " + e.getMessage());
        }
    }

    // -----------------------------
    //   PUBLIC GET – VALIDATED
    // -----------------------------
    @GetMapping("/furnitures")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<List<EntityFurniture>> getFurnitureByStatusUser() {
        return ResponseEntity.ok(
                furnitureService.getFurnitureByStatus(FurnitureStatus.validated.name())
        );
    }

    // -----------------------------
    //   USER GET – VALIDATED
    // -----------------------------
    @GetMapping("/user/furnitures")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<?> getAllFurnitureForUser() {

        List<EntityFurniture> furnitures = furnitureService.getFurnitureByStatus("validated");

        return ResponseEntity.ok(furnitures);
    }

    // -----------------------------
    //   ADMIN – ON HOLD
    // -----------------------------
    @GetMapping("/admin/furnitures")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<List<EntityFurniture>> getFurnitureByStatusAdmin() {
        return ResponseEntity.ok(
                furnitureService.getFurnitureByStatus(FurnitureStatus.on_hold.name())
        );
    }

    @PutMapping("/admin/furnitures/{id}/status")
    public ResponseEntity<?> updateStatusFurniture(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        furnitureService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok("Status updated");
    }

    // -----------------------------
    //   USER – LIST OWN ITEMS
    // -----------------------------
    @GetMapping("/user/{id}/furnitures/onSell")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<List<EntityFurniture>> getFurnitureOnSellById(@PathVariable Long id) {
        return ResponseEntity.ok(furnitureService.getFurnitureByUser(id));
    }

    // -----------------------------
    //     PUBLIC GET BY ID
    // -----------------------------
    @GetMapping("/furnitures/{id}")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<?> getFurnitureById(@PathVariable Long id) {
        return ResponseEntity.ok(furnitureService.findById(id));
    }

    // -----------------------------
    //   USER – GET OWN FURNITURE
    // -----------------------------
    @GetMapping("/user/furnitures/{id}")
    @JsonView(Views.FurnitureOutput.class)
    public ResponseEntity<?> getUserFurnitureById(
            @PathVariable Long id,
            @AuthenticationPrincipal EntityUser userConnected
    ) {

        if (userConnected == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Utilisateur non connecté");
        }

        EntityFurniture furniture = furnitureService.findById(id);

        if (furniture == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Meuble introuvable");

        if (!furniture.getUser().getId().equals(userConnected.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès refusé");

        return ResponseEntity.ok(furniture);
    }

    // -----------------------------
    //     UPDATE (OWNER ONLY)
    // -----------------------------
    @PutMapping("/user/furnitures/modify/{id}")
    public ResponseEntity<?> updateFurniture(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String price,
            @RequestParam String status,
            @RequestParam String width,
            @RequestParam String height,
            @RequestParam String length,
            @RequestParam Long type_id,
            @RequestParam Long color_id,
            @RequestParam Long material_id,
            @RequestParam(required = false) MultipartFile image,
            @AuthenticationPrincipal EntityUser userConnected
    ) throws IOException {

        EntityFurniture furniture = furnitureService.getFurnitureById(id);

        if (furniture == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Furniture not found");

        if (!furniture.getUser().getId().equals(userConnected.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vous ne pouvez pas modifier ce meuble");

        return ResponseEntity.ok(
                furnitureService.updateFurniture(
                        id, name, description, price, status,
                        width, height, length, type_id, color_id, material_id, image
                )
        );
    }

    // -----------------------------
    //   DELETE (OWNER ONLY)
    // -----------------------------
    @DeleteMapping("/user/furnitures/delete/{id}")
    public ResponseEntity<?> deleteFurnitureById(@PathVariable Long id) {
        furnitureService.deleteFurniture(id);
        return ResponseEntity.ok("Meuble supprimé avec succès");
    }

    // -----------------------------
    //   FILTER
    // -----------------------------
    @GetMapping("/user/furnitures/filter/{material}/{color}/{type}")
    public ResponseEntity<List<EntityFurniture>> getFurnitureByFilters(
            @PathVariable String material,
            @PathVariable String color,
            @PathVariable String type
    ) {
        Long materialId = material.equals("all") ? null : Long.parseLong(material);
        Long colorId = color.equals("all") ? null : Long.parseLong(color);
        Long typeId = type.equals("all") ? null : Long.parseLong(type);

        return ResponseEntity.ok(
                furnitureService.filterByMaterialColorType(
                        "validated", materialId, colorId, typeId
                )
        );
    }
}