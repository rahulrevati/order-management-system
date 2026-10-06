package com.ecommerce.order.event;

import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.common.kafka.producer.OrderEventProducer;
import com.ecommerce.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Single place that decides how an "order placed" notification leaves the
 * application.
 *
 * <ul>
 *   <li>It runs only after the order transaction has committed, so a rolled-back
 *       order never produces a notification.</li>
 *   <li>It runs asynchronously, so a slow broker or mail server cannot delay the
 *       HTTP response.</li>
 *   <li>Kafka enabled: the event is published and {@code NotificationConsumer}
 *       sends the email. Kafka disabled: the email is sent directly. Exactly one
 *       path is used, so customers never get two emails.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationListener {

    private final OrderEventProducer orderEventProducer;
    private final EmailService emailService;

    @Value("${app.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        try {
            if (kafkaEnabled) {
                orderEventProducer.publishOrderCreated(event);
            } else {
                emailService.sendOrderConfirmation(event);
            }
        } catch (Exception ex) {
            // The order is already committed; never let a notification
            // problem surface as a failed order.
            log.error("Failed to dispatch notification for order {}: {}",
                    event.getOrderNumber(), ex.getMessage(), ex);
        }
    }
}
