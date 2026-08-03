package com.ecommerce.common.kafka.config;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String ORDER_CREATED = "order-created";

    public static final String PAYMENT_COMPLETED = "payment-completed";
}