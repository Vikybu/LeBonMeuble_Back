package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.entities.EntityColor;
import com.LeBonMeuble.backend.services.ColorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ColorController {

    private final ColorService colorService;

    @GetMapping("/color")
    public ResponseEntity<List<EntityColor>> getColors() {
        List<EntityColor> colors = colorService.getAllColors();
        return ResponseEntity.ok(colors);
    }
}
