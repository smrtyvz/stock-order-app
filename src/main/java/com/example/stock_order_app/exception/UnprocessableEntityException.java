package com.example.stock_order_app.exception;

public class UnprocessableEntityException extends RuntimeException {

  public UnprocessableEntityException() {}

  public UnprocessableEntityException(String message) {
    super(message);
  }

}
