package com.turmericstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
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
@IgnoreExtraProperties
public class Order {

    @DocumentId
    private String id;

    private String userId;

    private String orderNumber;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

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

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
}
