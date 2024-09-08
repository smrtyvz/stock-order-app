package com.example.stock_order_app.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "asset_name", nullable = false)
  private String assetName;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "side", nullable = false)
  private String side;

  @Column(name = "size", nullable = false)
  private Double size;

  @Column(name = "price")
  private Double price;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "matched_at")
  private LocalDateTime matchedAt;

  @Column(name = "canceled_at")
  private LocalDateTime canceledAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id")
  private Customer customer;

}
