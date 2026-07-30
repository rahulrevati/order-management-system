//package com.ecommerce.order.controller;
//
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.order.dto.OrderRequest;
//import com.ecommerce.order.dto.OrderResponse;
//import com.ecommerce.order.dto.OrderStatusResponse;
//import com.ecommerce.order.service.OrderService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class OrderController {
//
//    private final OrderService orderService;
//
//    @PostMapping
//    public ResponseEntity<OrderResponse> createOrder(
//            @Valid @RequestBody OrderRequest orderRequest,
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        OrderResponse orderResponse = orderService.createOrder(orderRequest, email);
//        return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
//        OrderResponse orderResponse = orderService.getOrderById(id);
//        return ResponseEntity.ok(orderResponse);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<OrderResponse>> getUserOrders(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        List<OrderResponse> orders = orderService.getUserOrders(email);
//        return ResponseEntity.ok(orders);
//    }
//
//    @GetMapping("/status/{status}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable OrderStatus status) {
//        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
//        return ResponseEntity.ok(orders);
//    }
//
//    @PutMapping("/{id}/status")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<OrderStatusResponse> updateOrderStatus(
//            @PathVariable Long id,
//            @RequestParam OrderStatus status) {
//        OrderStatusResponse orderStatusResponse = orderService.updateOrderStatus(id, status);
//        return ResponseEntity.ok(orderStatusResponse);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
//        orderService.deleteOrder(id);
//        return ResponseEntity.noContent().build();
//    }
//}
