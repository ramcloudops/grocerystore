package com.turmericstore.util;

public class AppConstants {
    // Collection names
    public static final String COLLECTION_PRODUCTS = "products";
    public static final String COLLECTION_CATEGORIES = "categories";
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_ORDERS = "orders";
    public static final String COLLECTION_CARTS = "carts";
    public static final String COLLECTION_PAYMENTS = "payments";

    // Pagination defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    // Tax and shipping constants
    public static final double TAX_RATE = 0.08; // 8%
    public static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public static final double STANDARD_SHIPPING_COST = 5.99;

    // Cloud Storage paths
    public static final String PRODUCT_IMAGES_PATH = "products";
    public static final String CATEGORY_IMAGES_PATH = "categories";

    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
