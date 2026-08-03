package com.ecommerce.notification.service;

import com.ecommerce.common.kafka.event.OrderPlacedEvent;

public interface EmailService {

    void sendOrderConfirmation(OrderPlacedEvent event);

}