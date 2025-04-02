package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.Order;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class OrderRepository {

    private final Firestore firestore;
    private final CollectionReference orderCollection;

    @Autowired
    public OrderRepository(Firestore firestore) {
        this.firestore = firestore;
        this.orderCollection = firestore.collection(AppConstants.COLLECTION_ORDERS);
    }

    public List<Order> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = orderCollection.orderBy("createdAt", Query.Direction.DESCENDING).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Order> orders = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            orders.add(document.toObject(Order.class));
        }

        return orders;
    }

    public Optional<Order> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = orderCollection.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Order.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Order> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = orderCollection.whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Order> orders = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            orders.add(document.toObject(Order.class));
        }

        return orders;
    }

    public Optional<Order> findByOrderNumber(String orderNumber) throws ExecutionException, InterruptedException {
        Query query = orderCollection.whereEqualTo("orderNumber", orderNumber);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.of(documents.get(0).toObject(Order.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Order> findByStatus(Order.OrderStatus status) throws ExecutionException, InterruptedException {
        Query query = orderCollection.whereEqualTo("status", status)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Order> orders = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            orders.add(document.toObject(Order.class));
        }

        return orders;
    }

    public Order save(Order order) throws ExecutionException, InterruptedException {
        if (order.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = orderCollection.document();
            order.setId(docRef.getId());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(currentTime);
        }
        order.setUpdatedAt(currentTime);

        // Save to Firestore
        DocumentReference docRef = orderCollection.document(order.getId());
        ApiFuture<WriteResult> future = docRef.set(order);
        future.get(); // Wait for the operation to complete

        return order;
    }

    public void updateStatus(String id, Order.OrderStatus status) throws ExecutionException, InterruptedException {
        DocumentReference docRef = orderCollection.document(id);
        ApiFuture<WriteResult> future = docRef.update(
                "status", status,
                "updatedAt", System.currentTimeMillis()
        );
        future.get(); // Wait for the operation to complete
    }

    public void updatePaymentStatus(String id, Order.PaymentStatus paymentStatus) throws ExecutionException, InterruptedException {
        DocumentReference docRef = orderCollection.document(id);
        ApiFuture<WriteResult> future = docRef.update(
                "paymentStatus", paymentStatus,
                "updatedAt", System.currentTimeMillis()
        );
        future.get(); // Wait for the operation to complete
    }

    public List<Order> findRecentOrders(int limit) throws ExecutionException, InterruptedException {
        Query query = orderCollection.orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Order> orders = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            orders.add(document.toObject(Order.class));
        }

        return orders;
    }
}
