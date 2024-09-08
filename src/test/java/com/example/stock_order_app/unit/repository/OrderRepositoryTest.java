package com.example.stock_order_app.unit.repository;

import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.model.entity.Order;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.repository.OrderRepository;
import com.example.stock_order_app.unit.TestObjectFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class OrderRepositoryTest {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private AssetRepository assetRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  public void OrderRepository_Save_ReturnsSavedOrder() {
    Customer customer = customerRepository.save(TestObjectFactory.generateCustomer());
    assetRepository.save(generateAsset(customer, "TRY"));

    LocalDateTime createdAt = LocalDateTime.now();
    Order order = Order.builder().customer(customer).assetName("X").side("BUY").price(5d).size(50d)
        .createdAt(createdAt).status("PENDING").build();

    Order savedOrder = orderRepository.save(order);

    Assertions.assertNotNull(savedOrder);
    Assertions.assertEquals(customer, savedOrder.getCustomer());
    Assertions.assertEquals("X", savedOrder.getAssetName());
  }

  @Test
  public void OrderRepository_FindAllByCustomerAndCreatedAtBetween_ReturnsExistingOrders() {
    Customer customer = customerRepository.save(TestObjectFactory.generateCustomer());
    assetRepository.save(generateAsset(customer, "TRY"));

    LocalDateTime createdAt = LocalDateTime.now();
    Order order1 = Order.builder().customer(customer).assetName("X").side("BUY").price(5d).size(50d)
        .createdAt(createdAt).status("PENDING").build();
    Order order2 = Order.builder().customer(customer).assetName("Y").side("BUY").price(2d).size(60d)
        .createdAt(createdAt.plusSeconds(2)).status("PENDING").build();
    orderRepository.save(order1);
    orderRepository.save(order2);

    List<Order> customerOrders = orderRepository.findAllByCustomerAndCreatedAtBetween(customer,
        createdAt.minusSeconds(10), createdAt.plusSeconds(10));

    Assertions.assertNotNull(customerOrders);
    Assertions.assertEquals(2, customerOrders.size());
  }

  private Asset generateAsset(Customer customer, String assetName) {
    return Asset.builder()
        .customer(customer)
        .name(assetName)
        .size(10d)
        .usableSize(10d)
        .build();
  }

}
