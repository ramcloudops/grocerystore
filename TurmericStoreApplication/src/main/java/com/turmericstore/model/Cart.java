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
public class Cart {

    @DocumentId
    private String id;

    private String userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    private Double subtotal;

    private Long updatedAt;

    public void addItem(CartItem item) {
        // Check if product already exists in cart
        for (int i = 0; i < items.size(); i++) {
            CartItem existingItem = items.get(i);
            if (existingItem.getProductId().equals(item.getProductId())) {
                // Update quantity of existing item
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                recalculateSubtotal();
                return;
            }
        }

        // Add as new item
        items.add(item);
        recalculateSubtotal();
    }

    public void updateItemQuantity(String productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        recalculateSubtotal();
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        recalculateSubtotal();
    }

    public void clearCart() {
        items.clear();
        subtotal = 0.0;
    }

    public void recalculateSubtotal() {
        subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}
