package com.ecommerce.payment.mapper;

import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.orderNumber", target = "orderNumber")
    @Mapping(source = "createdAt", target = "paymentDate")
    PaymentResponse toResponse(Payment payment);
}