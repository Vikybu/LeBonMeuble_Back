package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.entities.EntityType;
import com.LeBonMeuble.backend.repositories.TypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeService {

    private final TypeRepository typeRepository;

    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public List<EntityType> getAllTypes() {
        return typeRepository.findAll();
    }
}