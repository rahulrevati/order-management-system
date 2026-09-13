package com.ecommerce.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BusinessMetrics {

    private final Counter ordersCreatedCounter;
    private final Counter emailsSentCounter;
    private final Counter kafkaMessagesCounter;

    public BusinessMetrics(MeterRegistry meterRegistry) {

        ordersCreatedCounter = Counter.builder("orders.created")
                .description("Total Orders Created")
                .register(meterRegistry);

        emailsSentCounter = Counter.builder("emails.sent")
                .description("Total Emails Sent")
                .register(meterRegistry);

        kafkaMessagesCounter = Counter.builder("kafka.messages")
                .description("Total Kafka Messages Published")
                .register(meterRegistry);
    }

    public void incrementOrdersCreated() {
        ordersCreatedCounter.increment();
    }

    public void incrementEmailsSent() {
        emailsSentCounter.increment();
    }

    public void incrementKafkaMessages() {
        kafkaMessagesCounter.increment();
    }
}