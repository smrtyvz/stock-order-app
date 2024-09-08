package com.example.stock_order_app.repository;

import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.model.entity.Order;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByCustomerAndCreatedAtBetween(Customer customer, LocalDateTime from, LocalDateTime to);

  @Transactional
  default Order updateOrInsert(Order entity) {
    return (entity);
  }

}
