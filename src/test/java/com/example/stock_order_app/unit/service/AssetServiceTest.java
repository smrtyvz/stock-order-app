package com.example.stock_order_app.unit.service;

import static org.mockito.Mockito.when;

import com.example.stock_order_app.exception.BadRequestException;
import com.example.stock_order_app.exception.UnprocessableEntityException;
import com.example.stock_order_app.model.dto.AssetDto;
import com.example.stock_order_app.model.dto.CustomerAssetDto;
import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.service.impl.AssetServiceImpl;
import com.example.stock_order_app.unit.TestObjectFactory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

  @Mock
  private AssetRepository assetRepository;
  @Mock
  private CustomerRepository customerRepository;
  @InjectMocks
  private AssetServiceImpl assetServiceImpl;

  private Customer customer;

  @BeforeEach
  void setUp() {
    customer = TestObjectFactory.generateCustomer();
  }

  @Test
  void AssetService_GetAssetsByCustomer_ReturnsExistingAssets() {
    Asset asset1 = Asset.builder().name("TRY").customer(customer).size(10.0)
        .usableSize(10.0).build();
    Asset asset2 = Asset.builder().name("X").customer(customer).size(10.0)
        .usableSize(10.0).build();

    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));
    when(assetRepository.findAllByCustomer(Mockito.any(Customer.class))).thenReturn(
        List.of(asset1, asset2));

    CustomerAssetDto customerAssetDto = assetServiceImpl.getCustomerAssets(customer.getId());

    Assertions.assertNotNull(customerAssetDto);
    Assertions.assertNotNull(customerAssetDto.getAssets());
    Assertions.assertEquals(2, customerAssetDto.getAssets().size());
  }

  @Test
  void AssetService_GetAssetByCustomerAndAssetName_ReturnsExistingAsset() {
    Asset asset1 = Asset.builder().name("TRY").customer(customer)
        .size(10.0)
        .usableSize(10.0).build();
    assetRepository.save(asset1);

    when(assetRepository.findByCustomerAndName(Mockito.any(Customer.class),
        Mockito.anyString())).thenReturn(asset1);

    AssetDto customerAsset = assetServiceImpl.getAssetByCustomerAndAssetName(
       customer, "TRY");

    Assertions.assertNotNull(customerAsset);
    Assertions.assertEquals("TRY", customerAsset.getAssetName());
  }

  @Test
  void AssetService_Deposit_CreatesNewTRYAsset() {
    Asset asset = Asset.builder().name("TRY").customer(customer).size(25d)
        .usableSize(25d).build();

    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));
    when(assetRepository.save(Mockito.any(Asset.class))).thenReturn(asset);

    AssetDto assetDto = assetServiceImpl.deposit(customer.getId(), "TRY", 25d);
    Assertions.assertNotNull(assetDto);
    Assertions.assertEquals(25d, assetDto.getSize());
    Assertions.assertEquals(25d, assetDto.getUsableSize());
  }

  @Test
  void AssetService_Deposit_UpdatesExistingTRYAsset() {
    Asset asset = Asset.builder().name("TRY").customer(customer).size(35d)
        .usableSize(35d).build();
    Asset existingAsset = Asset.builder().name("TRY").customer(customer).size(10d)
        .usableSize(10d).build();

    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(Mockito.any(Customer.class),
        Mockito.anyString())).thenReturn(existingAsset);
    when(assetRepository.save(Mockito.any(Asset.class))).thenReturn(asset);

    AssetDto assetDto = assetServiceImpl.deposit(customer.getId(), "TRY", 25d);
    Assertions.assertNotNull(assetDto);
    Assertions.assertEquals(35d, assetDto.getSize());
    Assertions.assertEquals(35d, assetDto.getUsableSize());
  }

  @Test
  void AssetService_Deposit_FailsWhen_AssetOtherThanTRY() {
    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));

    Assertions.assertThrows(BadRequestException.class,
        () -> assetServiceImpl.deposit(customer.getId(), "X", 25d));
  }

  @Test
  void AssetService_Withdrawal_DecreasesExistingAsset() {
    Asset asset = Asset.builder().name("TRY").customer(customer).size(10d)
        .usableSize(10d).build();
    Asset existingAsset = Asset.builder().name("TRY").customer(customer).size(35d)
        .usableSize(35d).build();

    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(Mockito.any(Customer.class),
        Mockito.anyString())).thenReturn(existingAsset);
    when(assetRepository.save(Mockito.any(Asset.class))).thenReturn(asset);

    AssetDto assetDto = assetServiceImpl.withdraw(customer.getId(), "TRY", 25d);
    Assertions.assertNotNull(assetDto);
    Assertions.assertEquals(10d, assetDto.getSize());
    Assertions.assertEquals(10d, assetDto.getUsableSize());
  }

  @Test
  void AssetService_Withdrawal_FailsWhen_TRYAmountNotEnough() {
    Asset existingAsset = Asset.builder().name("TRY").customer(customer).size(35d)
        .usableSize(35d).build();

    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));
    when(assetRepository.findByCustomerAndName(Mockito.any(Customer.class),
        Mockito.anyString())).thenReturn(existingAsset);

    Assertions.assertThrows(UnprocessableEntityException.class,
        () -> assetServiceImpl.withdraw(customer.getId(), "TRY", 50d));
  }

  @Test
  void AssetService_Withdrawal_FailsWhen_AssetOtherThanTRY() {
    when(customerRepository.findById(Mockito.any())).thenReturn(Optional.of(customer));

    Assertions.assertThrows(BadRequestException.class,
        () -> assetServiceImpl.withdraw(customer.getId(), "X", 25d));
  }

}
