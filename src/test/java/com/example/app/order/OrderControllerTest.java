package com.example.app.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.example.app.order.OrderController.API_PATH;
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
        LocalDateTime now = LocalDateTime.now();
        Order order1 = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customerName("テストユーザー")
                .totalAmount(new BigDecimal("10000.00"))
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Order order2 = Order.builder()
                .id(2L)
                .orderNumber("ORD-002")
                .customerName("別のユーザー")
                .totalAmount(new BigDecimal("5000.00"))
                .status(OrderStatus.CONFIRMED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2));

        // When & Then
        mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$[0].customerName").value("テストユーザー"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].orderNumber").value("ORD-002"))
                .andExpect(jsonPath("$[1].customerName").value("別のユーザー"))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
    }

    @Test
    void testGetOrderById_Found() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .customerName("取得テスト")
                .totalAmount(new BigDecimal("5000.00"))
                .status(OrderStatus.CONFIRMED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // When & Then
        mockMvc.perform(get("/orders/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-001"))
                .andExpect(jsonPath("$.customerName").value("取得テスト"))
                .andExpect(jsonPath("$.totalAmount").value(5000.00))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderRepository.findById(99999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/orders/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOrder() throws Exception {
        // Given
        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerName("新規顧客")
                .totalAmount(new BigDecimal("15000.00"))
                .build();

        LocalDateTime now = LocalDateTime.now();
        Order savedOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20251220-0001")
                .customerName("新規顧客")
                .totalAmount(new BigDecimal("15000.00"))
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When & Then
        mockMvc.perform(post(API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value(startsWith("ORD-")))
                .andExpect(jsonPath("$.customerName").value("新規顧客"))
                .andExpect(jsonPath("$.totalAmount").value(15000.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty());
    }
}
