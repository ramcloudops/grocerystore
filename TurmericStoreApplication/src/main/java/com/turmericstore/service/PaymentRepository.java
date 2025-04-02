package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.Payment;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class PaymentRepository {

    private final Firestore firestore;
    private final CollectionReference paymentCollection;

    @Autowired
    public PaymentRepository(Firestore firestore) {
        this.firestore = firestore;
        this.paymentCollection = firestore.collection(AppConstants.COLLECTION_PAYMENTS);
    }

    public List<Payment> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = paymentCollection.orderBy("createdAt", Query.Direction.DESCENDING).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Payment> payments = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            payments.add(document.toObject(Payment.class));
        }

        return payments;
    }

    public Optional<Payment> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = paymentCollection.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Payment.class));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Payment> findByOrderId(String orderId) throws ExecutionException, InterruptedException {
        Query query = paymentCollection.whereEqualTo("orderId", orderId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.of(documents.get(0).toObject(Payment.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Payment> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = paymentCollection.whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Payment> payments = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            payments.add(document.toObject(Payment.class));
        }

        return payments;
    }

    public Payment save(Payment payment) throws ExecutionException, InterruptedException {
        if (payment.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = paymentCollection.document();
            payment.setId(docRef.getId());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(currentTime);
        }
        payment.setUpdatedAt(currentTime);

        // Save to Firestore
        DocumentReference docRef = paymentCollection.document(payment.getId());
        ApiFuture<WriteResult> future = docRef.set(payment);
        future.get(); // Wait for the operation to complete

        return payment;
    }

    public void updateStatus(String id, Payment.PaymentStatus status) throws ExecutionException, InterruptedException {
        DocumentReference docRef = paymentCollection.document(id);
        ApiFuture<WriteResult> future = docRef.update(
                "status", status,
                "updatedAt", System.currentTimeMillis()
        );
        future.get(); // Wait for the operation to complete
    }
}
