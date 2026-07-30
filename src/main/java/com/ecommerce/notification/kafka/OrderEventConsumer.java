//package com.ecommerce.notification.kafka;
//
//import com.ecommerce.notification.service.NotificationService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OrderEventConsumer {
//
//    private final NotificationService notificationService;
//    private final ObjectMapper objectMapper;
//
//    @KafkaListener(topics = "order-created", groupId = "notification-group")
//    public void handleOrderCreatedEvent(String eventJson) {
//        try {
//            log.info("Received order created event: {}", eventJson);
//        } catch (Exception e) {
//            log.error("Error processing order created event", e);
//        }
//    }
//
//    @KafkaListener(topics = "payment-completed", groupId = "notification-group")
//    public void handlePaymentCompletedEvent(String eventJson) {
//        try {
//            log.info("Received payment completed event: {}", eventJson);
//        } catch (Exception e) {
//            log.error("Error processing payment completed event", e);
//        }
//    }
//
//    @KafkaListener(topics = "inventory-updated", groupId = "notification-group")
//    public void handleInventoryUpdatedEvent(String eventJson) {
//        try {
//            log.info("Received inventory updated event: {}", eventJson);
//        } catch (Exception e) {
//            log.error("Error processing inventory updated event", e);
//        }
//    }
//}
