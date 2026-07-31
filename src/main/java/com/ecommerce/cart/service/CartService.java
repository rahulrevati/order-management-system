package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartResponse;
import com.ecommerce.cart.dto.UpdateCartRequest;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getCart();

    CartResponse updateCartItem(Long cartItemId, UpdateCartRequest request);

    void removeCartItem(Long cartItemId);

    void clearCart();
}