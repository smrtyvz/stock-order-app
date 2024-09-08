package com.example.stock_order_app.service.impl;

import com.example.stock_order_app.exception.BadRequestException;
import com.example.stock_order_app.exception.ErrorMessages;
import com.example.stock_order_app.exception.ResourceNotFoundException;
import com.example.stock_order_app.exception.UnprocessableEntityException;
import com.example.stock_order_app.model.AssetType;
import com.example.stock_order_app.model.OrderRequestType;
import com.example.stock_order_app.model.OrderSide;
import com.example.stock_order_app.model.OrderStatus;
import com.example.stock_order_app.model.dto.CustomerOrderDto;
import com.example.stock_order_app.model.dto.OrderDto;
import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.model.entity.Order;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.repository.OrderRepository;
import com.example.stock_order_app.service.OrderService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final CustomerRepository customerRepository;
  private final AssetRepository assetRepository;

  @Autowired
  public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository,
      AssetRepository assetRepository) {
    this.orderRepository = orderRepository;
    this.customerRepository = customerRepository;
    this.assetRepository = assetRepository;
  }

  public OrderDto createOrder(Long customerId, OrderDto orderDto) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(
        () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    Asset customerTRYAsset = assetRepository.findByCustomerAndName(customer,
        AssetType.TRY.name());
    validateUsableSize(customerTRYAsset.getUsableSize(), orderDto.getPrice(), OrderSide.valueOf(orderDto.getOrderSide()));
    Order order = orderRepository.save(
        toOrder(customer, orderDto.getAssetName(), orderDto.getPrice(),
            orderDto.getOrderSide(),
            orderDto.getSize(), OrderStatus.PENDING));
    updateAssetSizeWithOrderSide(customerTRYAsset, order.getSide(), OrderRequestType.CREATE, orderDto.getPrice());
    assetRepository.save(customerTRYAsset);
    return toOrderDto(order);
  }

  public OrderDto cancelOrder(Long customerId, Long orderId) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(
        () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    Asset customerTRYAsset = assetRepository.findByCustomerAndName(customer,
        AssetType.TRY.name());
    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> new ResourceNotFoundException(ErrorMessages.ERROR_ORDER_NOT_FOUND + orderId));
    validateOrderStatus(order.getStatus(), OrderRequestType.CANCEL);
    order.setStatus(OrderStatus.CANCELED.name());
    order.setCanceledAt(LocalDateTime.now());
    Order canceledOrder = orderRepository.updateOrInsert(order);
    updateAssetSizeWithOrderSide(customerTRYAsset, order.getSide(), OrderRequestType.CANCEL, order.getPrice());
    assetRepository.save(customerTRYAsset);
    return toOrderDto(canceledOrder);
  }

  public OrderDto matchOrder(Long customerId, Long orderId) {
    Customer customer = customerRepository.findById(customerId).orElseThrow(
        () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    Asset customerTRYAsset = assetRepository.findByCustomerAndName(customer,
        AssetType.TRY.name());
    Order order = orderRepository.findById(orderId).orElseThrow(
        () -> new ResourceNotFoundException(ErrorMessages.ERROR_ORDER_NOT_FOUND + orderId));

    validateUsableSize(customerTRYAsset.getUsableSize(), order.getPrice(), OrderSide.valueOf(order.getSide()));
    validateOrderStatus(order.getStatus(), OrderRequestType.MATCH);

    order.setStatus(OrderStatus.MATCHED.name());
    order.setMatchedAt(LocalDateTime.now());
    Order matchedOrder = orderRepository.updateOrInsert(order);

    // update TRY assets
    updateAssetSizeWithOrderSide(customerTRYAsset, order.getSide(), OrderRequestType.MATCH, order.getPrice());
    assetRepository.save(customerTRYAsset);

    // update matched assets
    Asset orderedAsset = Asset.builder().name(order.getAssetName())
        .size(order.getSize()).usableSize(order.getSize()).customer(customer).build();
    assetRepository.save(orderedAsset);
    return toOrderDto(matchedOrder);
  }

  public CustomerOrderDto getOrdersByCustomerIdAndDateRange(Long customerId, LocalDateTime from,
      LocalDateTime to) {
    CustomerOrderDto customerOrderDto = new CustomerOrderDto();
    Optional<Customer> customer = customerRepository.findById(customerId);
    if (customer.isEmpty()) {
      customerOrderDto.setOrders(new ArrayList<>());
      return customerOrderDto;
    }
    customerOrderDto.setOrders(toOrderDtoList(orderRepository.findAllByCustomerAndCreatedAtBetween(customer.get(), from, to)));
    return customerOrderDto;
  }

  private void updateAssetSizeWithOrderSide(Asset customerTRYAsset, String orderSide,
      OrderRequestType orderRequestType, Double requestedAmount) {
    switch (OrderSide.valueOf(orderSide)) {
      case BUY -> operateBuy(customerTRYAsset, orderRequestType, requestedAmount);
      case SELL -> operateSell(customerTRYAsset, orderRequestType, requestedAmount);
    }
  }

  private void operateBuy(Asset customerTRYAsset, OrderRequestType orderRequestType,
      Double requestedAmount) {
    switch (orderRequestType) {
      case CREATE -> customerTRYAsset.setUsableSize(customerTRYAsset.getUsableSize() - requestedAmount);
      case CANCEL -> customerTRYAsset.setUsableSize(customerTRYAsset.getUsableSize() + requestedAmount);
      case MATCH -> customerTRYAsset.setSize(customerTRYAsset.getSize() - requestedAmount);
    }
  }

  private void operateSell(Asset customerTRYAsset, OrderRequestType orderRequestType,
      Double requestedAmount) {
    switch (orderRequestType) {
      case CREATE,CANCEL -> {}
      case MATCH -> customerTRYAsset.setSize(customerTRYAsset.getSize() + requestedAmount);
    }
  }

  private void validateOrderStatus(String orderStatus, OrderRequestType orderRequestType) {
    if (!OrderStatus.PENDING.name().equals(orderStatus)) {
      switch (orderRequestType) {
        case CANCEL -> throw new UnprocessableEntityException(
            ErrorMessages.ERROR_NOT_CANCEL_ORDER + orderStatus);
        case MATCH ->  throw new UnprocessableEntityException(
            ErrorMessages.ERROR_NOT_MATCH_ORDER + orderStatus);
      }
    }
  }

  private void validateUsableSize(Double usableSize, Double orderedAmount, OrderSide orderSide) {
    if (OrderSide.BUY.equals(orderSide) && usableSize < orderedAmount) {
      throw new BadRequestException(ErrorMessages.ERROR_TRY_ASSET_NOT_ENOUGH);
    }
  }

  private Order toOrder(Customer customer, String assetName, Double price, String orderSide,
      Double orderSize, OrderStatus orderStatus) {
    return Order.builder()
        .customer(customer)
        .assetName(assetName)
        .price(price)
        .side(orderSide)
        .size(orderSize)
        .status(orderStatus.name())
        .createdAt(LocalDateTime.now())
        .build();
  }

  private OrderDto toOrderDto(Order order) {
    return OrderDto.builder()
        .orderId(order.getId())
        .orderSide(order.getSide())
        .assetName(order.getAssetName())
        .status(order.getStatus())
        .size(order.getSize())
        .price(order.getPrice())
        .createdAt(order.getCreatedAt())
        .matchedAt(order.getMatchedAt())
        .canceledAt(order.getCanceledAt())
        .build();
  }

  private List<OrderDto> toOrderDtoList(List<Order> orders) {
    return orders.stream().map(this::toOrderDto).toList();
  }
}
