// IngredientsService.java
package com.luxury.service;

import com.luxury.dto.IngredientDTO;
import com.luxury.entity.Ingredient;
import com.luxury.repository.IngredientRepository;
import com.luxury.repository.ProductRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ProductRepository productRepository;
     @Transactional
    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream().map(this::toDTO).toList();
    }
     @Transactional
    public IngredientDTO getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(this::toDTO).orElse(null);
    }
     @Transactional
    public IngredientDTO createIngredient(IngredientDTO dto) {
    Ingredient ingredient = fromDTO(dto);
    return toDTO(ingredientRepository.save(ingredient));
}
    @Transactional
public IngredientDTO updateIngredient(Long id, IngredientDTO dto) {
    return ingredientRepository.findById(id).map(existing -> {
        existing.setName(dto.getName());
        existing.setQuantity(dto.getQuantity());
        existing.setUnit(dto.getUnit());
        existing.setType(dto.getType());
        existing.setPrice(dto.getPrice());

        if (dto.getProductId() != null) {
            productRepository.findById(dto.getProductId()).ifPresent(existing::setProduct);
        }

        return toDTO(ingredientRepository.save(existing));
    }).orElse(null);
}

     @Transactional
    public boolean deleteIngredient(Long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private IngredientDTO toDTO(Ingredient ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setIngredientId(ingredient.getIngredientId());

        if (ingredient.getProduct() != null) {
            dto.setProductId(ingredient.getProduct().getProductId()); // <-- Dòng này là bắt buộc
            dto.setProductName(ingredient.getProduct().getName());
        } else {
            dto.setProductId(null);
            dto.setProductName(null);
        }

        dto.setName(ingredient.getName());
        dto.setQuantity(ingredient.getQuantity());
        dto.setUnit(ingredient.getUnit());
        dto.setType(ingredient.getType());
        dto.setPrice(ingredient.getPrice());
        return dto;
    }
    private Ingredient fromDTO(IngredientDTO dto) {
    Ingredient ingredient = new Ingredient();
    ingredient.setIngredientId(dto.getIngredientId());
    ingredient.setName(dto.getName());
    ingredient.setQuantity(dto.getQuantity());
    ingredient.setUnit(dto.getUnit());
    ingredient.setType(dto.getType());
    ingredient.setPrice(dto.getPrice());

    if (dto.getProductId() != null) {
        productRepository.findById(dto.getProductId()).ifPresent(ingredient::setProduct);
    }

    return ingredient;
    }
}