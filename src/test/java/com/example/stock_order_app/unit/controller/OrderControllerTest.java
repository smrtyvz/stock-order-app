package com.example.stock_order_app.unit.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

import com.example.stock_order_app.controller.OrderController;
import com.example.stock_order_app.model.dto.CustomerOrderDto;
import com.example.stock_order_app.model.dto.OrderDto;
import com.example.stock_order_app.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private OrderService orderService;

  @Test
  public void OrderController_CreateOrder_ReturnsCreated() throws Exception {
    OrderDto orderDto = OrderDto.builder().orderId(1L).orderSide("BUY").status("PENDING")
        .assetName("TRY").price(50d).size(5d).build();
    when(orderService.createOrder(1L, orderDto)).thenReturn(orderDto);

    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.post("/api/customers/1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderDto)));

    response.andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(
            MockMvcResultMatchers.header()
                .string("Location", containsString("/api/customers/1/orders/1")));
  }

  @Test
  public void OrderController_GetCustomerOrders_ReturnsOk() throws Exception {
    CustomerOrderDto responseDto = new CustomerOrderDto();
    responseDto.setCustomerId(1L);
    List<OrderDto> orderDtos = Arrays.asList(
        OrderDto.builder().orderId(1L).orderSide("BUY").assetName("TRY").size(50d).build(),
        OrderDto.builder().orderId(1L).orderSide("BUY").assetName("X").size(50d).build()
    );
    responseDto.setOrders(orderDtos);

    LocalDateTime dateFrom = LocalDateTime.now();
    LocalDateTime dateTo = LocalDateTime.now();

    when(orderService.getOrdersByCustomerIdAndDateRange(1L, dateFrom,
        dateTo)).thenReturn(responseDto);

    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/customers/1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .queryParam("from", dateFrom.toString())
            .queryParam("to", dateTo.toString())
            .content(objectMapper.writeValueAsString(orderDtos)));

    response.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.orders.size()", CoreMatchers.is(orderDtos.size())));

  }

}
