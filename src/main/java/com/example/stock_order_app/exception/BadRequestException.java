package com.example.stock_order_app.exception;

public class BadRequestException extends RuntimeException {

  public BadRequestException() {
  }

  public BadRequestException(String message) {
    super(message);
  }
}
