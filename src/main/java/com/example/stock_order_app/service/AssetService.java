package com.example.stock_order_app.service;

import com.example.stock_order_app.model.dto.AssetDto;
import com.example.stock_order_app.model.dto.CustomerAssetDto;
import com.example.stock_order_app.model.entity.Customer;

public interface AssetService {

  AssetDto getAssetByCustomerAndAssetName(Customer customer, String name);
  CustomerAssetDto getCustomerAssets(Long customerId);
  AssetDto withdraw(Long customerId, String assetType, double requestedAmount);
  AssetDto deposit(Long customerId, String assetName, double amount);

}
