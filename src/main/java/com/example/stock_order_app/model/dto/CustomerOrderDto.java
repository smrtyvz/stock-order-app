package com.example.stock_order_app.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderDto {

  private Long customerId;
  private List<OrderDto> orders;
}
