package com.ecommerce.common.kafka.producer;

import com.ecommerce.common.kafka.config.KafkaTopics;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void publishOrderCreated(OrderPlacedEvent event) {

        log.info("Publishing OrderPlacedEvent : {}", event.getOrderNumber());

        kafkaTemplate.send(KafkaTopics.ORDER_CREATED, event);
    }
}