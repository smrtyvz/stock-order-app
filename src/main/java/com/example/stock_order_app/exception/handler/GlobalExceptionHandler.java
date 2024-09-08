package com.example.stock_order_app.exception.handler;

import com.example.stock_order_app.exception.BadRequestException;
import com.example.stock_order_app.exception.ResourceNotFoundException;
import com.example.stock_order_app.exception.UnprocessableEntityException;
import com.example.stock_order_app.model.dto.ErrorResponseDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = UnprocessableEntityException.class)
  public ResponseEntity<ErrorResponseDto> handleUnprocessableEntityException(
      UnprocessableEntityException e) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(new ErrorResponseDto(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value()));
  }

  @ExceptionHandler(value = BadRequestException.class)
  public ResponseEntity<ErrorResponseDto> handleUnprocessableEntityException(
      BadRequestException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
  }

  @ExceptionHandler(value = ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleUnprocessableEntityException(
      ResourceNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponseDto(e.getMessage(), HttpStatus.NOT_FOUND.value()));
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) ->{
      String fieldName = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      errors.put(fieldName, message);
    });
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

}
