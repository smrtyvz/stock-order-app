package com.example.stock_order_app.unit.repository;

import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import com.example.stock_order_app.repository.AssetRepository;
import com.example.stock_order_app.repository.CustomerRepository;
import com.example.stock_order_app.unit.TestObjectFactory;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AssetRepositoryTest {

  @Autowired
  private AssetRepository assetRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  public void AssetRepository_FindByCustomerAndAssetName_ReturnsExistingAsset() {
    Customer customer = customerRepository.save(TestObjectFactory.generateCustomer());

    Asset asset = generateAsset(customer, "TRY");
    assetRepository.save(asset);

    Asset savedAsset = assetRepository.findByCustomerAndName(customer, "TRY");

    Assertions.assertNotNull(savedAsset);
  }

  @Test
  public void AssetRepository_FindByCustomer_ReturnsExistingAsset() {
    Customer customer = customerRepository.save(TestObjectFactory.generateCustomer());
    Asset asset1 = generateAsset(customer, "TRY");
    Asset asset2 = generateAsset(customer, "X");

    assetRepository.save(asset1);
    assetRepository.save(asset2);

    List<Asset> customerSavedAssets = assetRepository.findAllByCustomer(customer);

    Assertions.assertNotNull(customerSavedAssets);
    Assertions.assertEquals(2, customerSavedAssets.size());
  }

  private Asset generateAsset(Customer customer, String assetName) {
    return Asset.builder()
        .customer(customer)
        .name(assetName)
        .size(10d)
        .usableSize(10d)
        .build();
  }

}
