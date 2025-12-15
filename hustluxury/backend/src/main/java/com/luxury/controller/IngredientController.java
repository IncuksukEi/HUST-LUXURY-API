package com.luxury.controller;

import com.luxury.dto.IngredientDTO;
import com.luxury.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@PreAuthorize("hasRole('ADMIN')")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public List<IngredientDTO> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        IngredientDTO dto = ingredientService.getIngredientById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
public ResponseEntity<IngredientDTO> createIngredient(@RequestBody IngredientDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(ingredientService.createIngredient(dto));
}

@PutMapping("/{id}")
public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id, @RequestBody IngredientDTO dto) {
    IngredientDTO updated = ingredientService.updateIngredient(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        return ingredientService.deleteIngredient(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
