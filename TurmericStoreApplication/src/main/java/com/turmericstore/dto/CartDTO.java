package com.turmericstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private String id;

    private String userId;

    @Builder.Default
    private List<CartItemDTO> items = new ArrayList<>();

    private Double subtotal;

    private Long updatedAt;

    // Additional fields
    private Integer itemCount;
    private Double tax;
    private Double shippingCost;
    private Double total;
}
