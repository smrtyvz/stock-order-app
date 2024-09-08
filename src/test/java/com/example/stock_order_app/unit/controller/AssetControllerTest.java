package com.example.stock_order_app.unit.controller;

import static org.mockito.Mockito.when;

import com.example.stock_order_app.controller.AssetController;
import com.example.stock_order_app.model.dto.AssetDto;
import com.example.stock_order_app.model.dto.CustomerAssetDto;
import com.example.stock_order_app.service.AssetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
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

@WebMvcTest(controllers = AssetController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AssetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private AssetService assetService;

  @Test
  public void AssetController_GetCustomerAssets_ReturnsAssets() throws Exception {
    long customerId = 1L;
    AssetDto assetDto = AssetDto.builder().assetName("TRY").size(10d).usableSize(10d).build();
    CustomerAssetDto responseDto = CustomerAssetDto.builder().customerId(customerId).assets(
        Arrays.asList(assetDto)).build();
    when(assetService.getCustomerAssets(customerId)).thenReturn(responseDto);

    ResultActions response = mockMvc.perform(
        MockMvcRequestBuilders.get("/api/customers/1/assets").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(responseDto)));

    response.andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.assets.size()", CoreMatchers.is(responseDto.getAssets().size())));
  }

}
