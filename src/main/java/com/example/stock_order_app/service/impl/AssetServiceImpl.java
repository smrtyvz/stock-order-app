package com.example.stock_order_app.service.impl;

import com.example.stock_order_app.exception.BadRequestException;
import com.example.stock_order_app.exception.ErrorMessages;
import com.example.stock_order_app.exception.ResourceNotFoundException;
import com.example.stock_order_app.exception.UnprocessableEntityException;
import com.example.stock_order_app.model.AssetType;
import com.example.stock_order_app.model.dto.AssetDto;
import com.example.stock_order_app.model.dto.CustomerAssetDto;
import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.service.AssetService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssetServiceImpl implements AssetService {

  private final AssetRepository assetRepository;
  private final CustomerRepository customerRepository;

  @Autowired
  public AssetServiceImpl(AssetRepository assetRepository, CustomerRepository customerRepository) {
    this.assetRepository = assetRepository;
    this.customerRepository = customerRepository;
  }

  public AssetDto getAssetByCustomerAndAssetName(Customer customer, String name) {
    return toAssetDto(assetRepository.findByCustomerAndName(customer, name));
  }

  public CustomerAssetDto getCustomerAssets(Long customerId) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(
            () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    return CustomerAssetDto.builder()
        .customerId(customerId)
        .assets(toAssetDtoList(assetRepository.findAllByCustomer(customer)))
        .build();
  }

  public AssetDto withdraw(Long customerId, String assetType, double requestedAmount) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(
            () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    if (AssetType.fromName(assetType) == null && !AssetType.TRY.name().equals(assetType)) {
      throw new BadRequestException(ErrorMessages.ERROR_WITHDRAW_OPERATION_NOT_SUPPORTED);
    }
    Asset customerTRYAsset = assetRepository.findByCustomerAndName(customer, AssetType.TRY.name());
    if (customerTRYAsset == null || requestedAmount > customerTRYAsset.getUsableSize()) {
      throw new UnprocessableEntityException(ErrorMessages.ERROR_TRY_ASSET_NOT_ENOUGH);
    }
    customerTRYAsset.setSize(customerTRYAsset.getSize() - requestedAmount);
    customerTRYAsset.setUsableSize(customerTRYAsset.getUsableSize() - requestedAmount);
    Asset updatedCustomerTRYAsset =  assetRepository.save(customerTRYAsset);
    return toAssetDto(updatedCustomerTRYAsset);
  }

  public AssetDto deposit(Long customerId, String assetName, double amount) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(
            () -> new ResourceNotFoundException(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND + customerId));
    if (AssetType.fromName(assetName) == null && !AssetType.TRY.name().equals(assetName)) {
      throw new BadRequestException(ErrorMessages.ERROR_DEPOSIT_OPERATION_NOT_SUPPORTED);
    }
    Asset customerTRYAsset = assetRepository.findByCustomerAndName(customer, AssetType.TRY.name());
    if (customerTRYAsset == null) {
      Asset createdAsset = assetRepository.save(
          Asset.builder().customer(customer).usableSize(amount).size(amount)
              .name(AssetType.TRY.name()).build());
      return toAssetDto(createdAsset);
    }
    customerTRYAsset.setSize(customerTRYAsset.getSize() + amount);
    customerTRYAsset.setUsableSize(customerTRYAsset.getUsableSize() + amount);
    Asset updatedCustomerTRYAsset = assetRepository.save(customerTRYAsset);
    return toAssetDto(updatedCustomerTRYAsset);
  }

  private AssetDto toAssetDto(Asset asset) {
    return AssetDto.builder()
        .assetName(asset.getName())
        .size(asset.getSize())
        .usableSize(asset.getUsableSize()).build();
  }

  private List<AssetDto> toAssetDtoList(List<Asset> assets) {
    return assets.stream().map(this::toAssetDto).toList();
  }

}
