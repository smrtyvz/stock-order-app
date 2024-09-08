package com.example.stock_order_app.exception;

public class ErrorMessages {

  public static final String ERROR_CUSTOMER_NOT_FOUND = "Customer not found with id : ";
  public static final String ERROR_ORDER_NOT_FOUND = "Order not found with id : ";
  public static final String ERROR_NOT_CANCEL_ORDER = "Cannot cancel the order, order status is : ";
  public static final String ERROR_NOT_MATCH_ORDER = "Cannot match the order, order status is : ";
  public static final String ERROR_WITHDRAW_OPERATION_NOT_SUPPORTED= "Withdraw operation not supported for provided asset type";
  public static final String ERROR_DEPOSIT_OPERATION_NOT_SUPPORTED= "Deposit operation not supported for provided asset type";
  public static final String ERROR_TRY_ASSET_NOT_ENOUGH= "Customer does not have enough TRY assets";

}
