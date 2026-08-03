package com.ecommerce.common.kafka.consumer;

import com.ecommerce.common.kafka.config.KafkaTopics;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.notification.service.EmailService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "ecommerce-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consume(OrderPlacedEvent event) {

        log.info("====================================");
        log.info("Order Event Received");
        log.info("Order Number : {}", event.getOrderNumber());
        log.info("Customer     : {}", event.getEmail());
        log.info("Amount       : {}", event.getTotalAmount());
        log.info("====================================");

        emailService.sendOrderConfirmation(event);
    }
}