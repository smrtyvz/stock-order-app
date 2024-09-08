package com.example.stock_order_app.unit;

import com.example.stock_order_app.model.entity.Customer;
import java.time.LocalDateTime;

public class TestObjectFactory {

  public static Customer generateCustomerWithoutId() {
    return Customer.builder()
        .firstName("John")
        .lastName("Smith")
        .createdAt(LocalDateTime.now())
        .build();
  }

  public static Customer generateCustomer() {
    Customer customer = new Customer();
    customer.setId(1L);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setFirstName("John");
    customer.setLastName("Smith");
    return customer;
  }

}
