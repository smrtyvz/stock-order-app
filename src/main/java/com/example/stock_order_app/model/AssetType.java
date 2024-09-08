package com.example.stock_order_app.model;

public enum AssetType {
  TRY("Turkish Lira"), X("Asset X"), Y("Asset Y"), Z("Asset Z");

  private AssetType(String name) {
    this.name = name;
  }

  private final String name;

  public static AssetType fromName(String name) {
    for (AssetType assetType : AssetType.values()) {
      if (assetType.name.equals(name)) {
        return assetType;
      }
    }
    return null;
  }
}
