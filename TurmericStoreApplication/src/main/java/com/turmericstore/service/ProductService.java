package com.turmericstore.service;

import com.turmericstore.dto.ProductDTO;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.Product;
import com.turmericstore.repository.ProductRepository;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryService categoryService, ModelMapperUtil modelMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @Cacheable(value = "products")
    public List<ProductDTO> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return enrichProductDTOs(modelMapper.toProductDTOs(products));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch products", e);
        }
    }

    @Cacheable(value = "product-details", key = "#id")
    public ProductDTO getProductById(String id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
            return enrichProductDTO(modelMapper.toProductDTO(product));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch product with id: " + id, e);
        }
    }

    @Cacheable(value = "products", key = "'category_' + #categoryId")
    public List<ProductDTO> getProductsByCategory(String categoryId) {
        try {
            List<Product> products = productRepository.findByCategoryId(categoryId);
            return enrichProductDTOs(modelMapper.toProductDTOs(products));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch products for category: " + categoryId, e);
        }
    }

    @Cacheable(value = "products", key = "'featured'")
    public List<ProductDTO> getFeaturedProducts() {
        try {
            List<Product> products = productRepository.findByFeatured(true);
            return enrichProductDTOs(modelMapper.toProductDTOs(products));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch featured products", e);
        }
    }

    public List<ProductDTO> searchProducts(String keyword) {
        try {
            List<Product> products = productRepository.search(keyword);
            return enrichProductDTOs(modelMapper.toProductDTOs(products));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to search products with keyword: " + keyword, e);
        }
    }

    public List<ProductDTO> getProductsByIds(List<String> ids) {
        try {
            List<Product> products = productRepository.findByIds(ids);
            return enrichProductDTOs(modelMapper.toProductDTOs(products));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch products by ids", e);
        }
    }

    @CacheEvict(value = {"products", "product-details"}, allEntries = true)
    public ProductDTO createProduct(ProductDTO productDTO) {
        try {
            Product product = modelMapper.toProduct(productDTO);
            product.setId(null); // Ensure we're creating a new product
            product.setCreatedAt(null); // Will be set in the repository
            product.setActive(true);

            Product savedProduct = productRepository.save(product);
            return enrichProductDTO(modelMapper.toProductDTO(savedProduct));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to create product", e);
        }
    }

    @CacheEvict(value = {"products", "product-details"}, allEntries = true)
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        try {
            // Verify the product exists
            productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            Product product = modelMapper.toProduct(productDTO);
            product.setId(id);

            Product updatedProduct = productRepository.save(product);
            return enrichProductDTO(modelMapper.toProductDTO(updatedProduct));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update product with id: " + id, e);
        }
    }

    @CacheEvict(value = {"products", "product-details"}, allEntries = true)
    public void deleteProduct(String id) {
        try {
            // Verify the product exists
            productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

            productRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to delete product with id: " + id, e);
        }
    }

    public long countProducts() {
        try {
            return productRepository.count();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to count products", e);
        }
    }

    // Helper methods for enriching DTOs with additional information
    private ProductDTO enrichProductDTO(ProductDTO productDTO) {
        if (productDTO.getCategoryId() != null) {
            try {
                String categoryName = categoryService.getCategoryById(productDTO.getCategoryId()).getName();
                productDTO.setCategoryName(categoryName);
            } catch (Exception e) {
                // If category not found, just leave categoryName as null
            }
        }
        return productDTO;
    }

    private List<ProductDTO> enrichProductDTOs(List<ProductDTO> productDTOs) {
        productDTOs.forEach(this::enrichProductDTO);
        return productDTOs;
    }
}
