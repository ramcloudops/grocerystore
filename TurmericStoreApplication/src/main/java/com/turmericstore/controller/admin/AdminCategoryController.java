package com.turmericstore.controller.admin;

import com.turmericstore.dto.CategoryDTO;
import com.turmericstore.service.CategoryService;
import com.turmericstore.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Category API", description = "Endpoints for category management (Admin only)")
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final StorageService storageService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService, StorageService storageService) {
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Creates a new category (Admin only)")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Updates an existing category (Admin only)")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable String id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Deletes a category (Admin only)")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Upload category image", description = "Uploads a category image (Admin only)")
    public ResponseEntity<String> uploadCategoryImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.uploadCategoryImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping("/delete-image")
    @Operation(summary = "Delete category image", description = "Deletes a category image (Admin only)")
    public ResponseEntity<Void> deleteCategoryImage(@RequestParam String imageUrl) {
        storageService.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
