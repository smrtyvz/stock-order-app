package com.example.stock_order_app.repository;

import com.example.stock_order_app.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
