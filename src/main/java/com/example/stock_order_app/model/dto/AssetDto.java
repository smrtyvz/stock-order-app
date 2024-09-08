package com.example.stock_order_app.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetDto {

  private String assetName;
  private Double size;
  private Double usableSize;

}
