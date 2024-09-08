package com.example.stock_order_app.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {

  private Long orderId;
  @NotBlank(message = "Asset name can not be null")
  private String assetName;
  private String orderSide;
  @Positive(message = "Asset size should be greater than 0")
  private Double size;
  @Positive(message = "Price should be greater than 0")
  private Double price;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime matchedAt;
  private LocalDateTime canceledAt;

}
