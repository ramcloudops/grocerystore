package com.turmericstore.controller;

import com.turmericstore.dto.OrderDTO;
import com.turmericstore.service.OrderService;
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

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "Endpoints for order management")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user orders", description = "Retrieves all orders for the current user")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        // The user ID will need to be resolved from the UserDetails in a real application
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
        // In a real application, you'd verify that the order belongs to the current user or if the user is an admin
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get order by number", description = "Retrieves a specific order by its order number")
    public ResponseEntity<OrderDTO> getOrderByNumber(@PathVariable String orderNumber, @AuthenticationPrincipal UserDetails userDetails) {
        // In a real application, you'd verify that the order belongs to the current user or if the user is an admin
        OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create order", description = "Creates a new order")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderDTO orderDTO, @AuthenticationPrincipal UserDetails userDetails) {
        // Set the user ID from the authenticated user
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        orderDTO.setUserId(userId);

        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
}