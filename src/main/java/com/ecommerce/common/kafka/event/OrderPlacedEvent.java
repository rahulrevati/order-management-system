package com.ecommerce.common.kafka.event;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedEvent {

    private Long orderId;

    private String orderNumber;

    private Long userId;

    private String email;

    private BigDecimal totalAmount;
}