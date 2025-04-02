package com.turmericstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Category {

    @DocumentId
    private String id;

    private String name;

    private String description;

    private String imageUrl;

    private String parentId; // For hierarchical categories

    private Boolean active;

    private Integer displayOrder;

    private Long createdAt;

    private Long updatedAt;
}
