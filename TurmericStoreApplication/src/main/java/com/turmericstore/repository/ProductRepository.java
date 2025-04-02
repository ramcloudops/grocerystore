package com.turmericstore.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.turmericstore.model.Product;
import com.turmericstore.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Repository
public class ProductRepository {

    private final Firestore firestore;
    private final CollectionReference productCollection;

    @Autowired
    public ProductRepository(Firestore firestore) {
        this.firestore = firestore;
        this.productCollection = firestore.collection(AppConstants.COLLECTION_PRODUCTS);
    }

    public List<Product> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = productCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            products.add(document.toObject(Product.class));
        }

        return products;
    }

    public Optional<Product> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = productCollection.document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return Optional.of(document.toObject(Product.class));
        } else {
            return Optional.empty();
        }
    }

    public List<Product> findByCategoryId(String categoryId) throws ExecutionException, InterruptedException {
        Query query = productCollection.whereEqualTo("categoryId", categoryId);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            products.add(document.toObject(Product.class));
        }

        return products;
    }

    public List<Product> findByFeatured(boolean featured) throws ExecutionException, InterruptedException {
        Query query = productCollection.whereEqualTo("featured", featured)
                .whereEqualTo("active", true);
        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            products.add(document.toObject(Product.class));
        }

        return products;
    }

    public List<Product> search(String keyword) throws ExecutionException, InterruptedException {
        // Basic search implementation - in a real app you might want to use Firestore's
        // array-contains or array-contains-any for better search capabilities
        String lowercaseKeyword = keyword.toLowerCase();

        // Get all products (this could be paginated in a real app)
        ApiFuture<QuerySnapshot> future = productCollection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> matchingProducts = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Product product = document.toObject(Product.class);
            // Check if keyword is in name or description
            if (product.getName().toLowerCase().contains(lowercaseKeyword) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(lowercaseKeyword))) {
                matchingProducts.add(product);
            }
        }

        return matchingProducts;
    }

    public List<Product> findByIds(List<String> ids) throws ExecutionException, InterruptedException {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = new ArrayList<>();

        // Firestore doesn't support 'in' queries with more than 10 values
        // So we need to batch the requests
        List<List<String>> batchedIds = new ArrayList<>();
        int batchSize = 10;

        for (int i = 0; i < ids.size(); i += batchSize) {
            batchedIds.add(ids.subList(i, Math.min(i + batchSize, ids.size())));
        }

        for (List<String> batch : batchedIds) {
            Query query = productCollection.whereIn(FieldPath.documentId(), batch);
            ApiFuture<QuerySnapshot> future = query.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                products.add(document.toObject(Product.class));
            }
        }

        return products;
    }

    public Product save(Product product) throws ExecutionException, InterruptedException {
        if (product.getId() == null) {
            // Create a new document with auto-generated ID
            DocumentReference docRef = productCollection.document();
            product.setId(docRef.getId());
        }

        // Set timestamps
        long currentTime = System.currentTimeMillis();
        if (product.getCreatedAt() == null) {
            product.setCreatedAt(currentTime);
        }
        product.setUpdatedAt(currentTime);

        // Save to Firestore
        DocumentReference docRef = productCollection.document(product.getId());
        ApiFuture<WriteResult> future = docRef.set(product);
        future.get(); // Wait for the operation to complete

        return product;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = productCollection.document(id);
        ApiFuture<WriteResult> future = docRef.delete();
        future.get(); // Wait for the operation to complete
    }

    public List<Product> findAllPaginated(int page, int size) throws ExecutionException, InterruptedException {
        // Pagination in Firestore requires a consistent ordering
        Query query = productCollection.orderBy("createdAt", Query.Direction.DESCENDING)
                .offset(page * size)
                .limit(size);

        ApiFuture<QuerySnapshot> future = query.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Product> products = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            products.add(document.toObject(Product.class));
        }

        return products;
    }

    public long count() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = productCollection.get();
        return future.get().size();
    }
}
