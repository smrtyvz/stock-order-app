package com.example.stock_order_app.service.impl;

import com.example.stock_order_app.model.dto.CustomerDto;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.service.CustomerService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

  private final CustomerRepository customerRepository;

  @Autowired
  public CustomerServiceImpl(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public CustomerDto createCustomer(CustomerDto customerDto) {
    Customer savedCustomer = customerRepository.save(toCustomer(customerDto));
    return toCustomerDto(savedCustomer);
  }

  private Customer toCustomer(CustomerDto customerDto) {
    return Customer.builder()
        .firstName(customerDto.getFirstName())
        .lastName(customerDto.getLastName())
        .createdAt(LocalDateTime.now())
        .build();
  }

  private CustomerDto toCustomerDto(Customer customer) {
    return CustomerDto.builder()
        .firstName(customer.getFirstName())
        .lastName(customer.getLastName())
        .id(customer.getId())
        .build();
  }

}
