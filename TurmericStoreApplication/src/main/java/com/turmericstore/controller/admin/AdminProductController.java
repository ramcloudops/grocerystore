package com.turmericstore.controller.admin;

import com.turmericstore.dto.ProductDTO;
import com.turmericstore.service.ProductService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Product API", description = "Endpoints for product management (Admin only)")
public class AdminProductController {

    private final ProductService productService;
    private final StorageService storageService;

    @Autowired
    public AdminProductController(ProductService productService, StorageService storageService) {
        this.productService = productService;
        this.storageService = storageService;
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Creates a new product (Admin only)")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.createProduct(productDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product (Admin only)")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Deletes a product (Admin only)")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload-image")
    @Operation(summary = "Upload product image", description = "Uploads a product image (Admin only)")
    public ResponseEntity<String> uploadProductImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.uploadProductImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @PostMapping("/upload-images")
    @Operation(summary = "Upload multiple product images", description = "Uploads multiple product images (Admin only)")
    public ResponseEntity<List<String>> uploadProductImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> imageUrls = storageService.uploadProductImages(files);
        return ResponseEntity.ok(imageUrls);
    }

    @DeleteMapping("/delete-image")
    @Operation(summary = "Delete product image", description = "Deletes a product image (Admin only)")
    public ResponseEntity<Void> deleteProductImage(@RequestParam String imageUrl) {
        storageService.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
