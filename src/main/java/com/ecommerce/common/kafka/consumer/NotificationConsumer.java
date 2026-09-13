package com.ecommerce.common.kafka.consumer;

import com.ecommerce.common.kafka.config.KafkaTopics;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        name = "app.kafka.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class NotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "${KAFKA_CONSUMER_GROUP:ecommerce-group}",
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