package com.turmericstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    private Double discountPrice;

    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;

    private String categoryId;

    private String unit;

    private List<String> imageUrls;

    private Map<String, String> attributes;

    private Boolean featured;

    private Boolean active;

    private Long createdAt;

    private Long updatedAt;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private Double weight;

    private String brand;

    private String country;

    // Computed fields
    private Boolean inStock;
    private Boolean isDiscounted;
    private String categoryName;
}
