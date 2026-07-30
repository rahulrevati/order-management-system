//package com.ecommerce.cart.controller;
//
//import com.ecommerce.cart.dto.CartRequest;
//import com.ecommerce.cart.dto.CartResponse;
//import com.ecommerce.cart.service.CartService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/cart")
//@RequiredArgsConstructor
//public class CartController {
//
//    private final CartService cartService;
//
//    @PostMapping
//    public ResponseEntity<CartResponse> addToCart(
//            @Valid @RequestBody CartRequest cartRequest,
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        CartResponse cartResponse = cartService.addToCart(cartRequest, email);
//        return new ResponseEntity<>(cartResponse, HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    public ResponseEntity<CartResponse> getCart(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        CartResponse cartResponse = cartService.getCart(email);
//        return ResponseEntity.ok(cartResponse);
//    }
//
//    @PutMapping("/{cartItemId}")
//    public ResponseEntity<CartResponse> updateCartItem(
//            @PathVariable Long cartItemId,
//            @RequestParam Integer quantity,
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        CartResponse cartResponse = cartService.updateCartItem(cartItemId, quantity, email);
//        return ResponseEntity.ok(cartResponse);
//    }
//
//    @DeleteMapping("/{cartItemId}")
//    public ResponseEntity<CartResponse> removeCartItem(
//            @PathVariable Long cartItemId,
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        CartResponse cartResponse = cartService.removeCartItem(cartItemId, email);
//        return ResponseEntity.ok(cartResponse);
//    }
//
//    @DeleteMapping
//    public ResponseEntity<Void> clearCart(
//            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
//        String email = userDetails.getUsername();
//        cartService.clearCart(email);
//        return ResponseEntity.noContent().build();
//    }
//}
