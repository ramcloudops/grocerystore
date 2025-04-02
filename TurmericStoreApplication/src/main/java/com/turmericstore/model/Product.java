package com.turmericstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Product {

    @DocumentId
    private String id;

    private String name;

    private String description;

    private Double price;

    private Double discountPrice;

    private Integer stock;

    private String categoryId;

    private String unit; // e.g., kg, g, pcs

    private List<String> imageUrls;

    private Map<String, String> attributes; // For variations like size, weight, etc.

    private Boolean featured;

    private Boolean active;

    private Long createdAt;

    private Long updatedAt;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    private Double weight; // For shipping calculations

    private String brand;

    private String country; // Country of origin

    public boolean isDiscounted() {
        return discountPrice != null && discountPrice < price;
    }

    public boolean isInStock() {
        return stock != null && stock > 0;
    }
}