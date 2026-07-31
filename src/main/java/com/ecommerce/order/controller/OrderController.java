package com.ecommerce.order.controller;

import com.ecommerce.common.response.ApiResponse;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.ecommerce.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder() {

        OrderResponse response = orderService.placeOrder();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Order placed successfully",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Orders retrieved successfully",
                        orderService.getMyOrders()));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long orderId) {

        OrderResponse response = orderService.cancelOrder(orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order cancelled successfully",
                        response));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderResponse response =
                orderService.updateOrderStatus(orderId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order status updated successfully",
                        response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Order retrieved successfully",
                        orderService.getOrderById(orderId)));
    }
}