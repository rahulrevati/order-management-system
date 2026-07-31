package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.dto.UpdateCartRequest;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse response = cartService.addToCart(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Product added to cart successfully",
                        response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart() {

        CartResponse response = cartService.getCart();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cart retrieved successfully",
                        response));
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartRequest request) {

        CartResponse response =
                cartService.updateCartItem(cartItemId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cart updated successfully",
                        response));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long cartItemId) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cart item removed successfully",
                        null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Cart cleared successfully",
                        null));
    }
}