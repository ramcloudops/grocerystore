package com.turmericstore.dto;

import com.turmericstore.model.Address;
import com.turmericstore.model.Order.OrderStatus;
import com.turmericstore.model.Order.PaymentStatus;
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
public class OrderDTO {

    private String id;

    private String userId;

    private String orderNumber;

    @Builder.Default
    private List<OrderItemDTO> items = new ArrayList<>();

    private OrderStatus status;

    private Double subtotal;

    private Double tax;

    private Double shippingCost;

    private Double discount;

    private Double total;

    private Address shippingAddress;

    private Address billingAddress;

    private String paymentId;

    private PaymentStatus paymentStatus;

    private String paymentMethod;

    private String trackingNumber;

    private String notes;

    private Long createdAt;

    private Long updatedAt;

    // Additional fields for display
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private Integer itemCount;
}
