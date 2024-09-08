package com.example.stock_order_app.unit.service;

import static org.mockito.Mockito.when;

import com.example.stock_order_app.model.dto.CustomerDto;
import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.service.impl.CustomerServiceImpl;
import com.example.stock_order_app.unit.TestObjectFactory;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;
  @InjectMocks
  private CustomerServiceImpl customerServiceImpl;

  @Test
  void CustomerService_CreateCustomer_ReturnsSavedCustomer() {
    CustomerDto customerDto = CustomerDto.builder().firstName("John").lastName("Smith").build();

    when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(TestObjectFactory.generateCustomer());
    CustomerDto savedCustomer = customerServiceImpl.createCustomer(customerDto);

    Assertions.assertNotNull(savedCustomer);
    Assertions.assertEquals(savedCustomer.getFirstName(), customerDto.getFirstName());
    Assertions.assertEquals(savedCustomer.getLastName(), customerDto.getLastName());
  }

}
