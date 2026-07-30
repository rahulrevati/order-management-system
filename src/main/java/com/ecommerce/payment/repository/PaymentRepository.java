//package com.ecommerce.payment.repository;
//
//import com.ecommerce.common.enums.PaymentMethod;
//import com.ecommerce.common.enums.PaymentStatus;
//import com.ecommerce.payment.entity.Payment;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface PaymentRepository extends JpaRepository<Payment, Long> {
//
//    Optional<Payment> findByOrderId(Long orderId);
//
//    List<Payment> findByStatus(PaymentStatus status);
//
//    List<Payment> findByPaymentMethod(PaymentMethod method);
//}
