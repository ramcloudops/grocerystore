package com.turmericstore.service;

import com.turmericstore.dto.CartDTO;
import com.turmericstore.dto.CartItemDTO;
import com.turmericstore.exception.ResourceNotFoundException;
import com.turmericstore.model.Cart;
import com.turmericstore.model.CartItem;
import com.turmericstore.model.Product;
import com.turmericstore.repository.CartRepository;
import com.turmericstore.repository.ProductRepository;
import com.turmericstore.util.AppConstants;
import com.turmericstore.util.ModelMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ModelMapperUtil modelMapper;

    @Autowired
    public CartService(CartRepository cartRepository, ProductRepository productRepository, ModelMapperUtil modelMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    public CartDTO getCartByUserId(String userId) {
        try {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createEmptyCart(userId));

            CartDTO cartDTO = modelMapper.toCartDTO(cart);
            calculateCartTotals(cartDTO);

            return cartDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch cart for user: " + userId, e);
        }
    }

    public CartDTO addToCart(String userId, CartItemDTO cartItemDTO) {
        try {
            // Verify the product exists
            Product product = productRepository.findById(cartItemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", cartItemDTO.getProductId()));

            // Check if the product is in stock
            if (product.getStock() < cartItemDTO.getQuantity()) {
                throw new IllegalArgumentException("Product does not have sufficient stock");
            }

            // Get or create cart for user
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> createEmptyCart(userId));

            // Enrich cart item with product details
            CartItem cartItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImageUrl(product.getImageUrls() != null && !product.getImageUrls().isEmpty()
                            ? product.getImageUrls().get(0) : null)
                    .price(product.getDiscountPrice() != null && product.getDiscountPrice() < product.getPrice()
                            ? product.getDiscountPrice() : product.getPrice())
                    .quantity(cartItemDTO.getQuantity())
                    .unit(product.getUnit())
                    .build();

            // Add to cart
            cart.addItem(cartItem);

            // Save cart
            Cart savedCart = cartRepository.save(cart);

            // Convert to DTO and calculate totals
            CartDTO cartDTO = modelMapper.toCartDTO(savedCart);
            calculateCartTotals(cartDTO);

            return cartDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to add item to cart", e);
        }
    }

    public CartDTO updateCartItemQuantity(String userId, String productId, int quantity) {
        try {
            // Get user's cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

            // Verify the product exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

            // Check if the product is in stock
            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("Product does not have sufficient stock");
            }

            // Update quantity
            cart.updateItemQuantity(productId, quantity);

            // Save cart
            Cart savedCart = cartRepository.save(cart);

            // Convert to DTO and calculate totals
            CartDTO cartDTO = modelMapper.toCartDTO(savedCart);
            calculateCartTotals(cartDTO);

            return cartDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to update cart item quantity", e);
        }
    }

    public CartDTO removeFromCart(String userId, String productId) {
        try {
            // Get user's cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

            // Remove item
            cart.removeItem(productId);

            // Save cart
            Cart savedCart = cartRepository.save(cart);

            // Convert to DTO and calculate totals
            CartDTO cartDTO = modelMapper.toCartDTO(savedCart);
            calculateCartTotals(cartDTO);

            return cartDTO;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to remove item from cart", e);
        }
    }

    public void clearCart(String userId) {
        try {
            // Get user's cart
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));

            // Clear cart
            cartRepository.clearCartItems(cart.getId());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Failed to clear cart", e);
        }
    }

    // Helper methods
    private Cart createEmptyCart(String userId) {
        return Cart.builder()
                .userId(userId)
                .subtotal(0.0)
                .updatedAt(System.currentTimeMillis())
                .build();
    }

    private void calculateCartTotals(CartDTO cartDTO) {
        // Calculate item count
        cartDTO.setItemCount(cartDTO.getItems().size());

        // Calculate tax
        double tax = cartDTO.getSubtotal() * AppConstants.TAX_RATE;
        cartDTO.setTax(tax);

        // Determine shipping cost
        double shippingCost = cartDTO.getSubtotal() >= AppConstants.FREE_SHIPPING_THRESHOLD
                ? 0.0
                : AppConstants.STANDARD_SHIPPING_COST;
        cartDTO.setShippingCost(shippingCost);

        // Calculate total
        double total = cartDTO.getSubtotal() + tax + shippingCost;
        cartDTO.setTotal(total);
    }
}
