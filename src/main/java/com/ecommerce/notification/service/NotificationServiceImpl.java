//package com.ecommerce.notification.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationServiceImpl implements NotificationService {
//
//    @Override
//    public void sendOrderConfirmationEmail(String toEmail, String orderNumber, String orderDetails) {
//        log.info("Sending order confirmation email to: {} for order: {}", toEmail, orderNumber);
//    }
//
//    @Override
//    public void sendPaymentConfirmationEmail(String toEmail, String paymentId, String amount) {
//        log.info("Sending payment confirmation email to: {} for payment: {} amount: {}", toEmail, paymentId, amount);
//    }
//
//    @Override
//    public void sendShippingNotificationEmail(String toEmail, String orderNumber, String trackingNumber) {
//        log.info("Sending shipping notification email to: {} for order: {} tracking: {}", toEmail, orderNumber, trackingNumber);
//    }
//
//    @Override
//    public void sendOrderStatusSms(String phoneNumber, String orderNumber, String status) {
//        log.info("Sending order status SMS to: {} for order: {} status: {}", phoneNumber, orderNumber, status);
//    }
//
//    @Override
//    public void sendPaymentStatusSms(String phoneNumber, String paymentId, String status) {
//        log.info("Sending payment status SMS to: {} for payment: {} status: {}", phoneNumber, paymentId, status);
//    }
//}
