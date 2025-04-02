package com.turmericstore.controller;

import com.turmericstore.dto.CartDTO;
import com.turmericstore.dto.CartItemDTO;
import com.turmericstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart API", description = "Endpoints for shopping cart management")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get user cart", description = "Retrieves the cart for the current user")
    public ResponseEntity<CartDTO> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authentication
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Add to cart", description = "Adds an item to the cart")
    public ResponseEntity<CartDTO> addToCart(
            @Valid @RequestBody CartItemDTO cartItemDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authentication
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        return ResponseEntity.ok(cartService.addToCart(userId, cartItemDTO));
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update cart item", description = "Updates the quantity of an item in the cart")
    public ResponseEntity<CartDTO> updateCartItem(
            @RequestParam String productId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authentication
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, productId, quantity));
    }

    @DeleteMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Remove from cart", description = "Removes an item from the cart")
    public ResponseEntity<CartDTO> removeFromCart(
            @RequestParam String productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authentication
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Clear cart", description = "Clears all items from the cart")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        // Get user ID from authentication
        String userId = userDetails.getUsername(); // This is temporary; in reality, you'd get the user ID associated with this email
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
