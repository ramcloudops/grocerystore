package com.turmericstore.util;

import com.turmericstore.dto.*;
import com.turmericstore.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelMapperUtil {

    // Product mapping
    public ProductDTO toProductDTO(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .stock(product.getStock())
                .categoryId(product.getCategoryId())
                .unit(product.getUnit())
                .imageUrls(product.getImageUrls())
                .attributes(product.getAttributes())
                .featured(product.getFeatured())
                .active(product.getActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .tags(product.getTags())
                .weight(product.getWeight())
                .brand(product.getBrand())
                .country(product.getCountry())
                .inStock(product.isInStock())
                .isDiscounted(product.isDiscounted())
                .build();
    }

    public List<ProductDTO> toProductDTOs(List<Product> products) {
        return products.stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    public Product toProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .discountPrice(productDTO.getDiscountPrice())
                .stock(productDTO.getStock())
                .categoryId(productDTO.getCategoryId())
                .unit(productDTO.getUnit())
                .imageUrls(productDTO.getImageUrls())
                .attributes(productDTO.getAttributes())
                .featured(productDTO.getFeatured())
                .active(productDTO.getActive())
                .createdAt(productDTO.getCreatedAt())
                .updatedAt(productDTO.getUpdatedAt())
                .tags(productDTO.getTags())
                .weight(productDTO.getWeight())
                .brand(productDTO.getBrand())
                .country(productDTO.getCountry())
                .build();
    }

    // Category mapping
    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParentId())
                .active(category.getActive())
                .displayOrder(category.getDisplayOrder())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public List<CategoryDTO> toCategoryDTOs(List<Category> categories) {
        return categories.stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
    }

    public Category toCategory(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        return Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .imageUrl(categoryDTO.getImageUrl())
                .parentId(categoryDTO.getParentId())
                .active(categoryDTO.getActive())
                .displayOrder(categoryDTO.getDisplayOrder())
                .createdAt(categoryDTO.getCreatedAt())
                .updatedAt(categoryDTO.getUpdatedAt())
                .build();
    }

    // User mapping
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .addresses(user.getAddresses())
                .roles(user.getRoles())
                .active(user.getActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .fullName(user.getFullName())
                .build();
    }

    public User toUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .phoneNumber(userDTO.getPhoneNumber())
                .addresses(userDTO.getAddresses())
                .roles(userDTO.getRoles())
                .active(userDTO.getActive())
                .lastLogin(userDTO.getLastLogin())
                .createdAt(userDTO.getCreatedAt())
                .updatedAt(userDTO.getUpdatedAt())
                .build();
    }

    // Order mapping
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .items(order.getItems().stream()
                        .map(this::toOrderItemDTO)
                        .collect(Collectors.toList()))
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shippingCost(order.getShippingCost())
                .discount(order.getDiscount())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentId(order.getPaymentId())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .trackingNumber(order.getTrackingNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .itemCount(order.getItems().size())
                .build();
    }

    public Order toOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }

        return Order.builder()
                .id(orderDTO.getId())
                .userId(orderDTO.getUserId())
                .orderNumber(orderDTO.getOrderNumber())
                .items(orderDTO.getItems().stream()
                        .map(this::toOrderItem)
                        .collect(Collectors.toList()))
                .status(orderDTO.getStatus())
                .subtotal(orderDTO.getSubtotal())
                .tax(orderDTO.getTax())
                .shippingCost(orderDTO.getShippingCost())
                .discount(orderDTO.getDiscount())
                .total(orderDTO.getTotal())
                .shippingAddress(orderDTO.getShippingAddress())
                .billingAddress(orderDTO.getBillingAddress())
                .paymentId(orderDTO.getPaymentId())
                .paymentStatus(orderDTO.getPaymentStatus())
                .paymentMethod(orderDTO.getPaymentMethod())
                .trackingNumber(orderDTO.getTrackingNumber())
                .notes(orderDTO.getNotes())
                .createdAt(orderDTO.getCreatedAt())
                .updatedAt(orderDTO.getUpdatedAt())
                .build();
    }

    // OrderItem mapping
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemDTO.builder()
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .productImageUrl(orderItem.getProductImageUrl())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .unit(orderItem.getUnit())
                .notes(orderItem.getNotes())
                .build();
    }

    public OrderItem toOrderItem(OrderItemDTO orderItemDTO) {
        if (orderItemDTO == null) {
            return null;
        }

        return OrderItem.builder()
                .productId(orderItemDTO.getProductId())
                .productName(orderItemDTO.getProductName())
                .productImageUrl(orderItemDTO.getProductImageUrl())
                .price(orderItemDTO.getPrice())
                .quantity(orderItemDTO.getQuantity())
                .subtotal(orderItemDTO.getSubtotal())
                .unit(orderItemDTO.getUnit())
                .notes(orderItemDTO.getNotes())
                .build();
    }

    // Cart mapping
    public CartDTO toCartDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems().stream()
                        .map(this::toCartItemDTO)
                        .collect(Collectors.toList()))
                .subtotal(cart.getSubtotal())
                .updatedAt(cart.getUpdatedAt())
                .itemCount(cart.getItems().size())
                .build();
    }

    public Cart toCart(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }

        return Cart.builder()
                .id(cartDTO.getId())
                .userId(cartDTO.getUserId())
                .items(cartDTO.getItems().stream()
                        .map(this::toCartItem)
                        .collect(Collectors.toList()))
                .subtotal(cartDTO.getSubtotal())
                .updatedAt(cartDTO.getUpdatedAt())
                .build();
    }

    // CartItem mapping
    public CartItemDTO toCartItemDTO(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        return CartItemDTO.builder()
                .productId(cartItem.getProductId())
                .productName(cartItem.getProductName())
                .productImageUrl(cartItem.getProductImageUrl())
                .price(cartItem.getPrice())
                .quantity(cartItem.getQuantity())
                .unit(cartItem.getUnit())
                .subtotal(cartItem.getPrice() * cartItem.getQuantity())
                .build();
    }

    public CartItem toCartItem(CartItemDTO cartItemDTO) {
        if (cartItemDTO == null) {
            return null;
        }

        return CartItem.builder()
                .productId(cartItemDTO.getProductId())
                .productName(cartItemDTO.getProductName())
                .productImageUrl(cartItemDTO.getProductImageUrl())
                .price(cartItemDTO.getPrice())
                .quantity(cartItemDTO.getQuantity())
                .unit(cartItemDTO.getUnit())
                .build();
    }

    // Payment mapping
    public PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .receiptUrl(payment.getReceiptUrl())
                .paymentDetails(payment.getPaymentDetails())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public Payment toPayment(PaymentDTO paymentDTO) {
        if (paymentDTO == null) {
            return null;
        }

        return Payment.builder()
                .id(paymentDTO.getId())
                .orderId(paymentDTO.getOrderId())
                .userId(paymentDTO.getUserId())
                .amount(paymentDTO.getAmount())
                .currency(paymentDTO.getCurrency())
                .status(paymentDTO.getStatus())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .transactionId(paymentDTO.getTransactionId())
                .receiptUrl(paymentDTO.getReceiptUrl())
                .paymentDetails(paymentDTO.getPaymentDetails())
                .errorMessage(paymentDTO.getErrorMessage())
                .createdAt(paymentDTO.getCreatedAt())
                .updatedAt(paymentDTO.getUpdatedAt())
                .build();
    }

    public List<UserDTO> toUserDTOs(List<User> users) {
        return users.stream()
                .map(this::toUserDTO) // Using existing toUserDTO method
                .collect(Collectors.toList());
    }

}