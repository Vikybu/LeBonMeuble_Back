package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityColor;
import com.LeBonMeuble.backend.repositories.ColorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    private final ColorRepository colorRepository;

    public ColorService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<EntityColor> getAllColors() {
        return colorRepository.findAll();
    }
}


