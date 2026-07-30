//package com.ecommerce.order.repository;
//
//import com.ecommerce.auth.entity.User;
//import com.ecommerce.common.enums.OrderStatus;
//import com.ecommerce.order.entity.Order;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface OrderRepository extends JpaRepository<Order, Long> {
//
//    List<Order> findByUser(User user);
//
//    List<Order> findByStatus(OrderStatus status);
//}
