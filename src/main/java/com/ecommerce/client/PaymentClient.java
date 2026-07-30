//package com.ecommerce.client;
//
//import com.ecommerce.payment.dto.PaymentRequest;
//import com.ecommerce.payment.dto.PaymentResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Component
//public class PaymentClient {
//
//    private final WebClient webClient;
//
//    public PaymentClient(WebClient.Builder webClientBuilder,
//                         @Value("${payment.service.url:http://localhost:8082}") String paymentServiceUrl) {
//        this.webClient = webClientBuilder
//                .baseUrl(paymentServiceUrl)
//                .build();
//    }
//
//    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
//        return webClient.post()
//                .uri("/api/payments")
//                .bodyValue(paymentRequest)
//                .retrieve()
//                .bodyToMono(PaymentResponse.class)
//                .block();
//    }
//
//    public PaymentResponse getPaymentById(Long paymentId) {
//        return webClient.get()
//                .uri("/api/payments/{id}", paymentId)
//                .retrieve()
//                .bodyToMono(PaymentResponse.class)
//                .block();
//    }
//
//    public PaymentResponse getPaymentByOrderId(Long orderId) {
//        return webClient.get()
//                .uri("/api/payments/order/{orderId}", orderId)
//                .retrieve()
//                .bodyToMono(PaymentResponse.class)
//                .block();
//    }
//}
