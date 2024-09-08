package com.example.stock_order_app.service;

import com.example.stock_order_app.model.dto.CustomerOrderDto;
import com.example.stock_order_app.model.dto.OrderDto;
import java.time.LocalDateTime;

public interface OrderService {

  OrderDto createOrder(Long customerId, OrderDto orderDto);
  OrderDto cancelOrder(Long customerId, Long orderId);
  OrderDto matchOrder(Long customerId, Long orderId);
  CustomerOrderDto getOrdersByCustomerIdAndDateRange(Long customerId, LocalDateTime from,
      LocalDateTime to);

}
