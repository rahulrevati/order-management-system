//package com.ecommerce.payment.controller;
//
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import com.ecommerce.payment.dto.PaymentRequest;
//import com.ecommerce.payment.dto.PaymentResponse;
//import com.ecommerce.payment.service.PaymentService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/payments")
//@RequiredArgsConstructor
//public class PaymentController {
//
//    private final PaymentService paymentService;
//
//    @PostMapping
//    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
//        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
//        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
//        PaymentResponse paymentResponse = paymentService.getPaymentById(id);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @GetMapping("/order/{orderId}")
//    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable Long orderId) {
//        PaymentResponse paymentResponse = paymentService.getPaymentByOrderId(orderId);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @GetMapping("/status/{status}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
//        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
//        return ResponseEntity.ok(payments);
//    }
//
//    @GetMapping("/method/{method}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<PaymentResponse>> getPaymentsByMethod(@PathVariable PaymentMethod method) {
//        List<PaymentResponse> payments = paymentService.getPaymentsByMethod(method);
//        return ResponseEntity.ok(payments);
//    }
//
//    @PutMapping("/{id}/status")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<PaymentResponse> updatePaymentStatus(
//            @PathVariable Long id,
//            @RequestParam PaymentStatus status) {
//        PaymentResponse paymentResponse = paymentService.updatePaymentStatus(id, status);
//        return ResponseEntity.ok(paymentResponse);
//    }
//
//    @PostMapping("/{id}/refund")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
//        PaymentResponse paymentResponse = paymentService.refundPayment(id);
//        return ResponseEntity.ok(paymentResponse);
//    }
//}
