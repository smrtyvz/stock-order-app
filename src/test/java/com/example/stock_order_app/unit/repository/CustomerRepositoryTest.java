package com.example.stock_order_app.unit.repository;

import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.unit.TestObjectFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CustomerRepositoryTest {

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  public void CustomerRepository_Save_ReturnsSavedCustomer() {
    Customer savedCustomer = customerRepository.save(TestObjectFactory.generateCustomer());

    Assertions.assertNotNull(savedCustomer);
    Assertions.assertNotNull(savedCustomer.getId());
    Assertions.assertEquals("John", savedCustomer.getFirstName());
  }

  @Test
  public void CustomerRepository_FindById_ReturnsCustomer() {
    Customer customer = TestObjectFactory.generateCustomerWithoutId();

    customerRepository.save(customer);

    Customer customerById = customerRepository.findById(customer.getId()).orElse(null);

    Assertions.assertNotNull(customerById.getId());
    Assertions.assertEquals("John", customerById.getFirstName());
  }

}
