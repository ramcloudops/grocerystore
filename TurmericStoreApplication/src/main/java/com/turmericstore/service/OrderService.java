package com.turmericstore.service;

import com.turmericstore.dto.OrderDTO;
import com.turmericstore.dto.OrderItemDTO;
import com.turmericstore.exception.BadRequestException;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.Order;
import com.turmericstore.model.OrderItem;
import com.turmericstore.model.Product;
import com.turmericstore.repository.OrderRepository;
import com.turmericstore.repository.ProductRepository;
import com.turmericstore.util.AppConstants;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        UserService userService, ModelMapperUtil modelMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(modelMapper::toOrderDTO)
                    .collect(Collectors.toList());

            // Enrich with user information
            enrichOrderDTOs(orderDTOs);

            return orderDTOs;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch orders", e);
        }
    }

    public OrderDTO getOrderById(String id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
            OrderDTO orderDTO = modelMapper.toOrderDTO(order);

            // Enrich with user information
            enrichOrderDTO(orderDTO);

            return orderDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch order with id: " + id, e);
        }
    }

    public OrderDTO getOrderByOrderNumber(String orderNumber) {
        try {
            Order order = orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
            OrderDTO orderDTO = modelMapper.toOrderDTO(order);

            // Enrich with user information
            enrichOrderDTO(orderDTO);

            return orderDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch order with number: " + orderNumber, e);
        }
    }

    public List<OrderDTO> getOrdersByUserId(String userId) {
        try {
            List<Order> orders = orderRepository.findByUserId(userId);
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(modelMapper::toOrderDTO)
                    .collect(Collectors.toList());

            // Enrich with user information
            enrichOrderDTOs(orderDTOs);

            return orderDTOs;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch orders for user: " + userId, e);
        }
    }

    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            // Validate order
            if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
                throw new BadRequestException("Order must have at least one item");
            }

            // Check if products exist and are in stock
            validateOrderItems(orderDTO.getItems());

            // Create the order entity
            Order order = modelMapper.toOrder(orderDTO);

            // Generate order number if not provided
            if (order.getOrderNumber() == null) {
                order.setOrderNumber(generateOrderNumber());
            }

            // Set initial status if not provided
            if (order.getStatus() == null) {
                order.setStatus(Order.OrderStatus.PENDING);
            }

            // Set initial payment status if not provided
            if (order.getPaymentStatus() == null) {
                order.setPaymentStatus(Order.PaymentStatus.PENDING);
            }

            // Calculate order totals
            calculateOrderTotals(order);

            // Save the order
            Order savedOrder = orderRepository.save(order);

            // Update product stock (in a real application, this might be done in a transaction)
            updateProductStock(savedOrder.getItems());

            return modelMapper.toOrderDTO(savedOrder);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to create order", e);
        }
    }

    public OrderDTO updateOrderStatus(String id, Order.OrderStatus status) {
        try {
            // Verify the order exists
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

            // Update the status
            orderRepository.updateStatus(id, status);

            // Return the updated order
            order.setStatus(status);
            order.setUpdatedAt(System.currentTimeMillis());

            return modelMapper.toOrderDTO(order);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    public OrderDTO updatePaymentStatus(String id, Order.PaymentStatus paymentStatus) {
        try {
            // Verify the order exists
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

            // Update the payment status
            orderRepository.updatePaymentStatus(id, paymentStatus);

            // Return the updated order
            order.setPaymentStatus(paymentStatus);
            order.setUpdatedAt(System.currentTimeMillis());

            return modelMapper.toOrderDTO(order);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update payment status", e);
        }
    }

    public List<OrderDTO> getRecentOrders(int limit) {
        try {
            List<Order> orders = orderRepository.findRecentOrders(limit);
            List<OrderDTO> orderDTOs = orders.stream()
                    .map(modelMapper::toOrderDTO)
                    .collect(Collectors.toList());

            // Enrich with user information
            enrichOrderDTOs(orderDTOs);

            return orderDTOs;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch recent orders", e);
        }
    }

    // Helper methods
    private String generateOrderNumber() {
        // Simple implementation - in a real app you might want a more sophisticated approach
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validateOrderItems(List<OrderItemDTO> items) throws ExecutionException, InterruptedException {
        for (OrderItemDTO item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Product '" + product.getName() + "' does not have sufficient stock");
            }
        }
    }

    private void calculateOrderTotals(Order order) {
        // Calculate subtotal
        double subtotal = order.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setSubtotal(subtotal);

        // Calculate tax
        double tax = subtotal * AppConstants.TAX_RATE;
        order.setTax(tax);

        // Determine shipping cost
        double shippingCost = subtotal >= AppConstants.FREE_SHIPPING_THRESHOLD
                ? 0.0
                : AppConstants.STANDARD_SHIPPING_COST;
        order.setShippingCost(shippingCost);

        // Calculate total
        double total = subtotal + tax + shippingCost;
        if (order.getDiscount() != null) {
            total -= order.getDiscount();
        }
        order.setTotal(total);

        // Calculate item subtotals
        for (OrderItem item : order.getItems()) {
            item.setSubtotal(item.getPrice() * item.getQuantity());
        }
    }

    private void updateProductStock(List<OrderItem> items) throws ExecutionException, InterruptedException {
        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

            // Reduce stock
            product.setStock(product.getStock() - item.getQuantity());

            // Save product
            productRepository.save(product);
        }
    }

    private void enrichOrderDTO(OrderDTO orderDTO) {
        try {
            if (orderDTO.getUserId() != null) {
                // Add user details to the order
                userService.getUserById(orderDTO.getUserId());
                // In a real application, you might add more user details to the order DTO
            }
        } catch (Exception e) {
            // If user not found, just continue without enrichment
        }
    }

    private void enrichOrderDTOs(List<OrderDTO> orderDTOs) {
        orderDTOs.forEach(this::enrichOrderDTO);
    }
}
