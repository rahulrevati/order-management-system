package com.ecommerce.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {

    private Long cartItemId;

    private Long productId;

    private String productName;

    private String productImage;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;
}