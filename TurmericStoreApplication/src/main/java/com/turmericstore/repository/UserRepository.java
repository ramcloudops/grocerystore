package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.User;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private final Firestore firestore;
    private final CollectionReference userCollection;

    @Autowired
    public UserRepository(Firestore firestore) {
        this.firestore = firestore;
        this.userCollection = firestore.collection(AppConstants.COLLECTION_USERS);
    }

    public List<User> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = userCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            users.add(document.toObject(User.class));
        }

        return users;
    }

    public Optional<User> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = userCollection.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(User.class));
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) throws ExecutionException, InterruptedException {
        // Email should be unique, so we expect only one result
        Query query = userCollection.whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        if (!documents.isEmpty()) {
            return Optional.of(documents.get(0).toObject(User.class));
        } else {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = userCollection.whereEqualTo("email", email);
        ApiFuture<QuerySnapshot> future = query.get();
        return !future.get().isEmpty();
    }

    public User save(User user) throws ExecutionException, InterruptedException {
        if (user.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = userCollection.document();
            user.setId(docRef.getId());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(currentTime);
        }
        user.setUpdatedAt(currentTime);

        // Save to Firestore
        DocumentReference docRef = userCollection.document(user.getId());
        ApiFuture<WriteResult> future = docRef.set(user);
        future.get(); // Wait for the operation to complete

        return user;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = userCollection.document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get(); // Wait for the operation to complete
    }

    public void updateLastLogin(String id, long timestamp) throws ExecutionException, InterruptedException {
        DocumentReference docRef = userCollection.document(id);
        ApiFuture<WriteResult> future = docRef.update("lastLogin", timestamp, "updatedAt", timestamp);
        future.get(); // Wait for the operation to complete
    }
}
