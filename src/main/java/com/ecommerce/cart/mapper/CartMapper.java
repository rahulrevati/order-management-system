//package com.ecommerce.cart.mapper;
//
//import com.ecommerce.cart.dto.CartResponse;
//import com.ecommerce.cart.entity.Cart;
//import com.ecommerce.cart.entity.CartItem;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//public class CartMapper {
//
//    public static CartResponse toResponse(Cart cart) {
//        List<CartResponse.CartItemResponse> cartItemResponses = cart.getCartItems().stream()
//                .map(CartMapper::toCartItemResponse)
//                .toList();
//
//        BigDecimal totalAmount = cartItemResponses.stream()
//                .map(CartResponse.CartItemResponse::getSubtotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        return CartResponse.builder()
//                .id(cart.getId())
//                .userId(cart.getUser().getId())
//                .cartItems(cartItemResponses)
//                .totalAmount(totalAmount)
//                .createdAt(cart.getCreatedAt())
//                .updatedAt(cart.getUpdatedAt())
//                .build();
//    }
//
//    private static CartResponse.CartItemResponse toCartItemResponse(CartItem cartItem) {
//        BigDecimal subtotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//
//        return CartResponse.CartItemResponse.builder()
//                .id(cartItem.getId())
//                .productId(cartItem.getProduct().getId())
//                .productName(cartItem.getProduct().getName())
//                .productImage(cartItem.getProduct().getImageUrl())
//                .quantity(cartItem.getQuantity())
//                .price(cartItem.getPrice())
//                .subtotal(subtotal)
//                .build();
//    }
//}
