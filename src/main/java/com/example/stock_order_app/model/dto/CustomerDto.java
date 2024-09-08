package com.example.stock_order_app.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {

  private Long id;

  @NotBlank(message = "First name can not be null")
  private String firstName;

  @NotBlank(message = "Last name can not be null")
  private String lastName;

}
