package com.turmericstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private String id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;

    private String imageUrl;

    private String parentId;

    private Boolean active;

    private Integer displayOrder;

    private Long createdAt;

    private Long updatedAt;

    // For hierarchical display
    private String parentName;
    private Integer productCount;
}