package com.turmericstore.service;

import com.turmericstore.dto.CategoryDTO;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.Category;
import com.turmericstore.repository.CategoryRepository;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, ModelMapperUtil modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Cacheable(value = "categories")
    public List<CategoryDTO> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return modelMapper.toCategoryDTOs(categories);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }

    @Cacheable(value = "categories", key = "'active'")
    public List<CategoryDTO> getAllActiveCategories() {
        try {
            List<Category> categories = categoryRepository.findAllActive();
            return modelMapper.toCategoryDTOs(categories);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch active categories", e);
        }
    }

    @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(String id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
            return modelMapper.toCategoryDTO(category);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch category with id: " + id, e);
        }
    }

    @Cacheable(value = "categories", key = "'parent_' + #parentId")
    public List<CategoryDTO> getCategoriesByParentId(String parentId) {
        try {
            List<Category> categories = categoryRepository.findByParentId(parentId);
            return modelMapper.toCategoryDTOs(categories);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch subcategories for parent: " + parentId, e);
        }
    }

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        try {
            Category category = modelMapper.toCategory(categoryDTO);
            category.setId(null); // Ensure we're creating a new category
            category.setCreatedAt(null); // Will be set in the repository
            category.setActive(true);

            // Set default display order if not provided
            if (category.getDisplayOrder() == null) {
                category.setDisplayOrder(0);
            }

            Category savedCategory = categoryRepository.save(category);
            return modelMapper.toCategoryDTO(savedCategory);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to create category", e);
        }
    }

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO updateCategory(String id, CategoryDTO categoryDTO) {
        try {
            // Verify the category exists
            categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

            Category category = modelMapper.toCategory(categoryDTO);
            category.setId(id);

            Category updatedCategory = categoryRepository.save(category);
            return modelMapper.toCategoryDTO(updatedCategory);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update category with id: " + id, e);
        }
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(String id) {
        try {
            // Verify the category exists
            categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

            // Check if there are any subcategories
            List<Category> subcategories = categoryRepository.findByParentId(id);
            if (!subcategories.isEmpty()) {
                throw new IllegalStateException("Cannot delete category with subcategories");
            }

            // TODO: Check if there are any products in this category
            // If implemented, this would be added here

            categoryRepository.delete(id);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to delete category with id: " + id, e);
        }
    }
}
