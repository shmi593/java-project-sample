package com.example.app.controller.order;

import com.example.app.domain.order.Order;
import com.example.app.domain.order.OrderStatus;
import com.example.app.usecase.order.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.example.app.controller.order.OrderController.API_PATH;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    void testGetAllOrders_Empty() throws Exception {
        // Given
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get(API_PATH))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllOrders_WithData() throws Exception {
        // Given
        Order order1 = new Order(
                "ORD-001",
                "テストユーザー",
                10000.00,
                OrderStatus.PENDING
        );
        Order order2 = new Order(
                "ORD-002",
                "別のユーザー",
                5000.00,
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        // When & Then
        mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$[0].customerName").value("テストユーザー"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].orderNumber").value("ORD-002"))
                .andExpect(jsonPath("$[1].customerName").value("別のユーザー"))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
    }

    @Test
    void testGetOrderByOrderNumber_Found() throws Exception {
        // Given
        Order order = new Order(
                "ORD-001",
                "取得テスト",
                5000.00,
                OrderStatus.CONFIRMED
        );

        when(orderRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(get("/orders/{orderNumber}", "ORD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.customerName").value("取得テスト"))
                .andExpect(jsonPath("$.totalAmount").value(5000.00))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testGetOrderByOrderNumber_NotFound() throws Exception {
        // Given
        when(orderRepository.findByOrderNumber("NOT-EXIST")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/orders/{orderNumber}", "NOT-EXIST"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("新規顧客")
                .totalAmount(new BigDecimal("15000.00"))
                .build();

        Order savedOrder = new Order(
                "ORD-20251220-0001",
                "新規顧客",
                15000.00,
                OrderStatus.PENDING
        );

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value(startsWith("ORD-")))
                .andExpect(jsonPath("$.customerName").value("新規顧客"))
                .andExpect(jsonPath("$.totalAmount").value(15000.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
