package com.turmericstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class Payment {

    @DocumentId
    private String id;

    private String orderId;

    private String userId;

    private Double amount;

    private String currency;

    private PaymentStatus status;

    private String paymentMethod;

    private String transactionId;

    private String receiptUrl;

    private Map<String, Object> paymentDetails; // For gateway-specific details

    private String errorMessage;

    private Long createdAt;

    private Long updatedAt;

    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}
