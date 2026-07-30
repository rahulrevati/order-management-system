package com.ecommerce.common.constants;

public class AppConstants {

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";

    public static final String USER_ROLE_PREFIX = "ROLE_";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";

    public static final String ORDER_CREATED_TOPIC = "order-created";
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String INVENTORY_UPDATED_TOPIC = "inventory-updated";

    private AppConstants() {
    }
}
