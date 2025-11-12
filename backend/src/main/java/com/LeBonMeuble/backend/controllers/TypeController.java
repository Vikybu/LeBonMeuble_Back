package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.entities.EntityType;
import com.LeBonMeuble.backend.services.TypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TypeController {

    private final TypeService typeService;

    @GetMapping("/type")
    public ResponseEntity<List<EntityType>> getTypes() {
        List<EntityType> types = typeService.getAllTypes();
        return ResponseEntity.ok(types);
    }
}