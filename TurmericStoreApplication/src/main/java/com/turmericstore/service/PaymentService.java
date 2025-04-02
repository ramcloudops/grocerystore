package com.turmericstore.service;

import com.turmericstore.dto.PaymentDTO;
import com.turmericstore.exception.BadRequestException;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.Order;
import com.turmericstore.model.Payment;
import com.turmericstore.repository.OrderRepository;
import com.turmericstore.repository.PaymentRepository;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository,
                          OrderService orderService, ModelMapperUtil modelMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    public PaymentDTO processPayment(PaymentDTO paymentDTO) {
        try {
            // Verify the order exists
            Order order = orderRepository.findById(paymentDTO.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", paymentDTO.getOrderId()));

            // Verify the payment amount matches the order total
            if (!order.getTotal().equals(paymentDTO.getAmount())) {
                throw new BadRequestException("Payment amount does not match order total");
            }

            // Create payment record
            Payment payment = modelMapper.toPayment(paymentDTO);
            payment.setId(null); // Ensure we're creating a new payment
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setCreatedAt(System.currentTimeMillis());
            payment.setUpdatedAt(System.currentTimeMillis());

            // In a real application, this is where you would integrate with a payment gateway
            // For this implementation, we'll simulate a successful payment
            simulatePaymentProcessing(payment);

            // Save the payment
            Payment savedPayment = paymentRepository.save(payment);

            // Update order with payment information
            if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                orderService.updatePaymentStatus(order.getId(), Order.PaymentStatus.PAID);
            } else if (payment.getStatus() == Payment.PaymentStatus.FAILED) {
                orderService.updatePaymentStatus(order.getId(), Order.PaymentStatus.FAILED);
            }

            return modelMapper.toPaymentDTO(savedPayment);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to process payment", e);
        }
    }

    public PaymentDTO getPaymentById(String id) {
        try {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
            return modelMapper.toPaymentDTO(payment);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch payment with id: " + id, e);
        }
    }

    public PaymentDTO getPaymentByOrderId(String orderId) {
        try {
            Payment payment = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
            return modelMapper.toPaymentDTO(payment);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch payment for order: " + orderId, e);
        }
    }

    // Helper method to simulate payment processing
    // In a real application, this would be replaced with actual payment gateway integration
    private void simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing
        // For demonstration purposes, we'll simulate a success scenario 90% of the time
        boolean isSuccessful = Math.random() < 0.9;

        if (isSuccessful) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setTransactionId("TX-" + System.currentTimeMillis());
            payment.setReceiptUrl("https://receipt.example.com/" + payment.getTransactionId());

            // Add payment details
            Map<String, Object> paymentDetails = new HashMap<>();
            paymentDetails.put("paymentMethod", payment.getPaymentMethod());
            paymentDetails.put("processingTime", "2.3 seconds");
            payment.setPaymentDetails(paymentDetails);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setErrorMessage("Payment processing failed. Please try again.");
        }
    }
}