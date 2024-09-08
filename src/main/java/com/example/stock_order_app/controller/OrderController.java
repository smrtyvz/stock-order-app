package com.example.stock_order_app.controller;

import com.example.stock_order_app.model.dto.CustomerOrderDto;
import com.example.stock_order_app.model.dto.OrderDto;
import com.example.stock_order_app.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(path = "/api" ,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

  private final OrderService orderService;

  @Autowired
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping(path = "/customers/{customer_id}/orders")
  public ResponseEntity<OrderDto> createOrder(@PathVariable(name = "customer_id") Long customerId,@Valid @RequestBody OrderDto orderDto) {
    OrderDto order = orderService.createOrder(customerId, orderDto);
    return ResponseEntity.created(
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getOrderId()).toUri()).build();
  }

  @GetMapping(path = "/customers/{customer_id}/orders")
  public ResponseEntity<CustomerOrderDto> getAllOrdersByCustomerAndDateRange(
      @PathVariable(name = "customer_id") Long customerId,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(name = "from") LocalDateTime fromDate,
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(name = "to") LocalDateTime toDate) {
    CustomerOrderDto orders = orderService.getOrdersByCustomerIdAndDateRange(customerId, fromDate, toDate);
    return ResponseEntity.ok().body(orders);
  }

  @PutMapping(path = "/customers/{customer_id}/orders/{order_id}")
  public ResponseEntity<OrderDto> cancelOrder(@PathVariable(name = "customer_id") Long customerId,
      @PathVariable(name = "order_id") Long orderId) {
    orderService.cancelOrder(customerId, orderId);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping(path = "/customers/{customer_id}/orders/{order_id}")
  public ResponseEntity<OrderDto> matchOrder(@PathVariable(name = "customer_id") Long customerId,
      @PathVariable(name = "order_id") Long orderId) {
    orderService.matchOrder(customerId, orderId);
    return ResponseEntity.noContent().build();
  }

}
