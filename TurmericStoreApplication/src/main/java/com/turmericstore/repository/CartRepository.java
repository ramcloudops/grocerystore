package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.Cart;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class CartRepository {

    private final Firestore firestore;
    private final CollectionReference cartCollection;

    @Autowired
    public CartRepository(Firestore firestore) {
        this.firestore = firestore;
        this.cartCollection = firestore.collection(AppConstants.COLLECTION_CARTS);
    }

    public Optional<Cart> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = cartCollection.whereEqualTo("userId", userId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.of(documents.get(0).toObject(Cart.class));
        } else {
            return Optional.empty();
        }
    }

    public Cart save(Cart cart) throws ExecutionException, InterruptedException {
        if (cart.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = cartCollection.document();
            cart.setId(docRef.getId());
        }

        // Set updated timestamp
        cart.setUpdatedAt(System.currentTimeMillis());

        // Save to Firestore
        DocumentReference docRef = cartCollection.document(cart.getId());
        ApiFuture<WriteResult> future = docRef.set(cart);
        future.get(); // Wait for the operation to complete

        return cart;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = cartCollection.document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get(); // Wait for the operation to complete
    }

    public void clearCartItems(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = cartCollection.document(id);
        ApiFuture<WriteResult> future = docRef.update(
                "items", new ArrayList<>(),
                "subtotal", 0.0,
                "updatedAt", System.currentTimeMillis()
        );
        future.get(); // Wait for the operation to complete
    }
}