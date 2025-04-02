package com.turmericstore.controller;

import com.turmericstore.dto.PaymentDTO;
import com.turmericstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment API", description = "Endpoints for payment processing")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Process payment", description = "Processes a payment for an order")
    public ResponseEntity<PaymentDTO> processPayment(
            @Valid @RequestBody PaymentDTO paymentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Set the user ID from the authenticated user
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        paymentDTO.setUserId(userId);

        PaymentDTO processedPayment = paymentService.processPayment(paymentDTO);
        return new ResponseEntity<>(processedPayment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific payment by its ID")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment by order ID", description = "Retrieves a payment for a specific order")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}
