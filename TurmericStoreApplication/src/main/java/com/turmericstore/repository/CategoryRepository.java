package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.Category;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class CategoryRepository {

    private final Firestore firestore;
    private final CollectionReference categoryCollection;

    @Autowired
    public CategoryRepository(Firestore firestore) {
        this.firestore = firestore;
        this.categoryCollection = firestore.collection(AppConstants.COLLECTION_CATEGORIES);
    }

    public List<Category> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = categoryCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Category> categories = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            categories.add(document.toObject(Category.class));
        }

        return categories;
    }

    public List<Category> findAllActive() throws ExecutionException, InterruptedException {
        Query query = categoryCollection.whereEqualTo("active", true)
                .orderBy("displayOrder", Query.Direction.ASCENDING);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Category> categories = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            categories.add(document.toObject(Category.class));
        }

        return categories;
    }

    public Optional<Category> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = categoryCollection.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Category.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Category> findByParentId(String parentId) throws ExecutionException, InterruptedException {
        Query query;

        if (parentId == null) {
            // Find root categories (with no parent)
            query = categoryCollection.whereEqualTo("parentId", null);
        } else {
            query = categoryCollection.whereEqualTo("parentId", parentId);
        }

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Category> categories = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            categories.add(document.toObject(Category.class));
        }

        return categories;
    }

    public Category save(Category category) throws ExecutionException, InterruptedException {
        if (category.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = categoryCollection.document();
            category.setId(docRef.getId());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        if (category.getCreatedAt() == null) {
            category.setCreatedAt(currentTime);
        }
        category.setUpdatedAt(currentTime);

        // Save to Firestore
        DocumentReference docRef = categoryCollection.document(category.getId());
        ApiFuture<WriteResult> future = docRef.set(category);
        future.get(); // Wait for the operation to complete

        return category;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = categoryCollection.document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get(); // Wait for the operation to complete
    }
}