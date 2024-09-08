package com.example.stock_order_app.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.stock_order_app.exception.BadRequestException;
import com.example.stock_order_app.exception.UnprocessableEntityException;
import com.example.stock_order_app.model.OrderSide;
import com.example.stock_order_app.model.OrderStatus;
import com.example.stock_order_app.model.dto.OrderDto;
import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.model.entity.Order;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.repository.OrderRepository;
import com.example.stock_order_app.service.impl.OrderServiceImpl;
import com.example.stock_order_app.unit.TestObjectFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private AssetRepository assetRepository;
  @InjectMocks
  private OrderServiceImpl orderServiceImpl;

  private Customer customer;

  @BeforeEach
  void setUp() {
    customer = TestObjectFactory.generateCustomer();
  }

  @Test
  void OrderService_CreateOrder_ReturnsSavedOrder() {
    Order order = generateOrder(1L, customer, OrderStatus.PENDING);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(generateAsset(customer));
    when(orderRepository.save(any(Order.class))).thenReturn(order);

    OrderDto orderDto = OrderDto.builder()
        .orderSide("BUY")
        .assetName("X")
        .size(10d)
        .price(5d).build();

    OrderDto savedOrder = orderServiceImpl.createOrder(customer.getId(), orderDto);

    Assertions.assertNotNull(savedOrder);
    Assertions.assertEquals(OrderSide.BUY.name(), order.getSide());
    Assertions.assertEquals(orderDto.getPrice(), order.getPrice());
    Assertions.assertEquals(orderDto.getSize(), order.getSize());
    Assertions.assertNotNull(savedOrder.getCreatedAt());
  }

  @Test
  void OrderService_CreateOrder_FailsWhen_UsableSizeNotEnough() {
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(generateAsset(customer));

    OrderDto orderDto = OrderDto.builder()
        .orderSide("BUY")
        .assetName("X")
        .size(10d)
        .price(150d).build();

    Assertions.assertThrows(
        BadRequestException.class, () -> orderServiceImpl.createOrder(customer.getId(), orderDto));
  }

  @Test
  void OrderService_CancelOrder_CancelsOrder() {
    Order order = generateOrder(1L, customer, OrderStatus.PENDING);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(
        Asset.builder().name("TRY").customer(customer).size(10.0)
            .usableSize(10.0).build());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.updateOrInsert(any(Order.class))).thenReturn(order);

    OrderDto canceledOrder = orderServiceImpl.cancelOrder(customer.getId(), order.getId());

    Assertions.assertNotNull(canceledOrder);
    Assertions.assertEquals(OrderStatus.CANCELED, OrderStatus.valueOf(canceledOrder.getStatus()));
    Assertions.assertNotNull(canceledOrder.getCanceledAt());
  }

  @Test
  void OrderService_CancelOrder_FailsWhen_OrderStatusNotPending() {
    Order order = generateOrder(1L, customer, OrderStatus.MATCHED);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(
        Asset.builder().name("TRY").customer(customer).size(10.0)
            .usableSize(10.0).build());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    Assertions.assertThrows(
        UnprocessableEntityException.class, () -> orderServiceImpl.cancelOrder(customer.getId(), order.getId()));
  }

  @Test
  void OrderService_MatchOrder_MatchesGivenOrder() {
    Order order = generateOrder(1L, customer, OrderStatus.PENDING);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(
        Asset.builder().name("TRY").customer(customer).size(10.0)
            .usableSize(10.0).build());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
    when(orderRepository.updateOrInsert(any(Order.class))).thenReturn(order);

    OrderDto matchedOrder = orderServiceImpl.matchOrder(customer.getId(), order.getId());


    Assertions.assertNotNull(matchedOrder);
    Assertions.assertEquals(OrderStatus.MATCHED, OrderStatus.valueOf(matchedOrder.getStatus()));
    Assertions.assertNotNull(matchedOrder.getMatchedAt());
  }

  @Test
  void OrderService_MatchOrder_FailsWhen_OrderStatusNotPending() {
    Order order = generateOrder(1L, customer, OrderStatus.CANCELED);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(
        Asset.builder().name("TRY").customer(customer).size(10.0)
            .usableSize(10.0).build());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    Assertions.assertThrows(
        UnprocessableEntityException.class, () -> orderServiceImpl.matchOrder(customer.getId(), order.getId()));
  }

  @Test
  void OrderService_MatchOrder_FailsWhen_UsableSizeNotEnough() {
    Order order = generateOrder(1L, customer, OrderStatus.PENDING);
    when(customerRepository.findById(any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(any(), any())).thenReturn(
        Asset.builder().name("TRY").customer(customer).size(10.0)
            .usableSize(1.0).build());
    when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

    Assertions.assertThrows(
        BadRequestException.class, () -> orderServiceImpl.matchOrder(customer.getId(), order.getId()));
  }

  private Order generateOrder(Long id, Customer customer, OrderStatus orderStatus) {
    return Order.builder()
        .id(id)
        .assetName("X")
        .side("BUY")
        .size(10d)
        .price(5d)
        .customer(customer)
        .status(orderStatus.name())
        .createdAt(LocalDateTime.now())
        .build();
  }

  private Asset generateAsset(Customer customer) {
    return Asset.builder().name("TRY").customer(customer).size(10.0)
        .usableSize(10.0).build();
  }


}
