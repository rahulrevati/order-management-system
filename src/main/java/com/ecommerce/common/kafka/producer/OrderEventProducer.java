package com.ecommerce.common.kafka.producer;

import com.ecommerce.common.kafka.config.KafkaTopics;
import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.monitoring.BusinessMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
    private final BusinessMetrics businessMetrics;

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    public void publishOrderCreated(OrderPlacedEvent event) {

        if (!kafkaEnabled) {
            log.info(
                    "Kafka disabled. Skipping OrderPlacedEvent for order {}",
                    event.getOrderNumber()
            );
            return;
        }

        log.info(
                "Publishing OrderPlacedEvent: {}",
                event.getOrderNumber()
        );

        kafkaTemplate
                .send(KafkaTopics.ORDER_CREATED, event)
                .whenComplete((result, exception) -> {

                    if (exception != null) {
                        log.error(
                                "Failed to publish OrderPlacedEvent for order {}: {}",
                                event.getOrderNumber(),
                                exception.getMessage()
                        );
                        return;
                    }

                    businessMetrics.incrementKafkaMessages();

                    log.info(
                            "OrderPlacedEvent published successfully: {}",
                            event.getOrderNumber()
                    );
                });
    }
}