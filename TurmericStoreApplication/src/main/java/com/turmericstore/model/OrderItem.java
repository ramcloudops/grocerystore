package com.turmericstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String productId;

    private String productName;

    private String productImageUrl;

    private Double price;

    private Integer quantity;

    private Double subtotal;

    private String unit;

    // Any customizations or special instructions
    private String notes;
}
