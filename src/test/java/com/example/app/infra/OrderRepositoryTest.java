package com.example.app.infra;

import com.example.app.domain.order.OrderStatus;
import com.example.app.usecase.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository sut;

    @BeforeEach
    void setUp() {
        sut.deleteAll();
    }

    @Test
    void testSaveOrder() {
        // Given
        Order order = Order.builder()
                .orderNumber("TEST-001")
                .customerName("テスト太郎")
                .totalAmount(new BigDecimal("10000.00"))
                .status(OrderStatus.PENDING)
                .build();

        // When
        Order savedOrder = sut.save(order);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getOrderNumber()).isEqualTo("TEST-001");
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(savedOrder.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindByOrderNumber() {
        // Given
        Order order = Order.builder()
                .orderNumber("TEST-002")
                .customerName("テスト花子")
                .totalAmount(new BigDecimal("5000.00"))
                .status(OrderStatus.CONFIRMED)
                .build();
        sut.save(order);

        // When
        Optional<Order> found = sut.findByOrderNumber("TEST-002");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("テスト花子");
    }

    @Test
    void testFindByOrderNumber_NotFound() {
        // When
        Optional<Order> found = sut.findByOrderNumber("NOT-EXIST");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByStatus() {
        // Given
        Order pendingOrder = Order.builder()
                .orderNumber("TEST-003")
                .customerName("山田一郎")
                .totalAmount(new BigDecimal("3000.00"))
                .status(OrderStatus.PENDING)
                .build();

        Order completedOrder = Order.builder()
                .orderNumber("TEST-004")
                .customerName("山田二郎")
                .totalAmount(new BigDecimal("4000.00"))
                .status(OrderStatus.COMPLETED)
                .build();

        sut.save(pendingOrder);
        sut.save(completedOrder);

        // When
        List<Order> pendingOrders = sut.findByStatus(OrderStatus.PENDING);

        // Then
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).getOrderNumber()).isEqualTo("TEST-003");
    }

    @Test
    void testFindByCustomerNameContaining() {
        // Given
        Order order1 = Order.builder()
                .orderNumber("TEST-005")
                .customerName("鈴木太郎")
                .totalAmount(new BigDecimal("1000.00"))
                .status(OrderStatus.PENDING)
                .build();

        Order order2 = Order.builder()
                .orderNumber("TEST-006")
                .customerName("田中次郎")
                .totalAmount(new BigDecimal("2000.00"))
                .status(OrderStatus.PENDING)
                .build();

        sut.save(order1);
        sut.save(order2);

        // When
        List<Order> found = sut.findByCustomerNameContaining("鈴木");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getOrderNumber()).isEqualTo("TEST-005");
    }

    @Test
    void testFindAll() {
        // Given
        Order order1 = Order.builder()
                .orderNumber("TEST-007")
                .customerName("佐藤三郎")
                .totalAmount(new BigDecimal("6000.00"))
                .status(OrderStatus.SHIPPED)
                .build();

        Order order2 = Order.builder()
                .orderNumber("TEST-008")
                .customerName("佐藤四郎")
                .totalAmount(new BigDecimal("7000.00"))
                .status(OrderStatus.CANCELLED)
                .build();

        sut.save(order1);
        sut.save(order2);

        // When
        List<Order> allOrders = sut.findAll();

        // Then
        assertThat(allOrders).hasSize(2);
    }
}
