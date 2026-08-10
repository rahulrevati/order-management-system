package com.ecommerce.email;

import com.ecommerce.common.kafka.event.OrderPlacedEvent;
import com.ecommerce.monitoring.BusinessMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final BusinessMetrics businessMetrics;

    @Async
    @Override
    public void sendOrderConfirmation(OrderPlacedEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.getEmail());

        message.setSubject(
                "Order Confirmation - " + event.getOrderNumber());

        message.setText(
                "Dear Customer,\n\n" +
                        "Your order has been placed successfully.\n\n" +
                        "Order Number : " + event.getOrderNumber() + "\n" +
                        "Total Amount : ₹" + event.getTotalAmount() + "\n\n" +
                        "Thank you for shopping with us!"
        );

        mailSender.send(message);
        businessMetrics.incrementEmailsSent();

        log.info("Email sent successfully to {}", event.getEmail());
    }
}