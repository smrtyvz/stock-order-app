package com.example.stock_order_app.model.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerAssetDto {

  private Long customerId;
  private List<AssetDto> assets;
}
