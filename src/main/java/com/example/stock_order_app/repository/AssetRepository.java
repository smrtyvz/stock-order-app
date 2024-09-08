package com.example.stock_order_app.repository;

import com.example.stock_order_app.model.entity.Asset;
import com.example.stock_order_app.model.entity.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {

  List<Asset> findAllByCustomer(Customer customer);

  Asset findByCustomerAndName(Customer customer, String name);

}
