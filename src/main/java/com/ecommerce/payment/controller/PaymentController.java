package com.ecommerce.payment.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.payment.dto.CreatePaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> makePayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response =
                paymentService.makePayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payment completed successfully",
                        response));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment retrieved successfully",
                        paymentService.getPayment(paymentId)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Payment retrieved successfully",
                        paymentService.getPaymentByOrder(orderId)));
    }
}