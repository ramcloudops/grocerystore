package com.turmericstore.dto;

import com.turmericstore.model.Payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private String id;

    @NotBlank(message = "Order ID is required")
    private String orderId;

    private String userId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than or equal to 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    private PaymentStatus status;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String transactionId;

    private String receiptUrl;

    private Map<String, Object> paymentDetails;

    private String errorMessage;

    private Long createdAt;

    private Long updatedAt;

    // Additional fields
    private String orderNumber;
    private String customerName;
}
