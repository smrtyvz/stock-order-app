package com.example.stock_order_app.controller;

import com.example.stock_order_app.model.dto.AssetDto;
import com.example.stock_order_app.model.dto.AssetOperationDto;
import com.example.stock_order_app.model.dto.CustomerAssetDto;
import com.example.stock_order_app.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetController {

  private final AssetService assetService;

  @Autowired
  public AssetController(AssetService assetService) {
    this.assetService = assetService;
  }

  @GetMapping(path = "/customers/{customer_id}/assets")
  public ResponseEntity<CustomerAssetDto> getAllAssets(@PathVariable(name = "customer_id") Long customerId) {
    return ResponseEntity.ok(assetService.getCustomerAssets(customerId));
  }

  @PostMapping(path = "/customers/{customer_id}/assets/{asset_type}/deposit")
  public ResponseEntity<AssetDto> depositAsset(@PathVariable(name = "customer_id") Long customerId,
      @PathVariable(name = "asset_type") String assetType,
      @RequestBody AssetOperationDto assetOperationDto) {
    AssetDto assetDto = assetService.deposit(customerId, assetType, assetOperationDto.getAmount());
    return ResponseEntity.ok(assetDto);
  }

  @PostMapping(path = "/customers/{customer_id}/assets/{asset_type}/withdraw")
  public ResponseEntity<AssetDto> withdrawAsset(@PathVariable(name = "customer_id") Long customerId,
      @PathVariable(name = "asset_type") String assetType,
      @RequestBody AssetOperationDto assetOperationDto) {
    AssetDto assetDto = assetService.withdraw(customerId, assetType, assetOperationDto.getAmount());
    return ResponseEntity.ok(assetDto);
  }

}
