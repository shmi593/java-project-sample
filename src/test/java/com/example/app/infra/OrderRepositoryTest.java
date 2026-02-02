package com.example.app.infra;

import com.example.app.domain.order.Order;
import com.example.app.domain.order.OrderStatus;
import com.example.app.infra.order.OrderJpaRepository;
import com.example.app.infra.order.OrderMapper;
import com.example.app.infra.order.OrderRepositoryImpl;
import com.example.app.infra.order.OrderTable;
import com.example.app.usecase.order.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({OrderRepositoryImpl.class, OrderMapper.class})
class OrderRepositoryTest {

    // テスト用固定 UUID（UUIDv7 形式）
    private static final UUID TEST_UUID_1 = UUID.fromString("019469e0-0000-7000-8000-000000000001");
    private static final UUID TEST_UUID_2 = UUID.fromString("019469e0-0000-7000-8000-000000000002");
    private static final UUID TEST_UUID_3 = UUID.fromString("019469e0-0000-7000-8000-000000000003");
    private static final UUID TEST_UUID_4 = UUID.fromString("019469e0-0000-7000-8000-000000000004");
    private static final UUID TEST_UUID_5 = UUID.fromString("019469e0-0000-7000-8000-000000000005");
    private static final UUID TEST_UUID_6 = UUID.fromString("019469e0-0000-7000-8000-000000000006");
    private static final UUID TEST_UUID_7 = UUID.fromString("019469e0-0000-7000-8000-000000000007");
    private static final UUID TEST_UUID_8 = UUID.fromString("019469e0-0000-7000-8000-000000000008");

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private EntityManager entityManager;

    private OrderRepository sut;

    @BeforeEach
    void setUp() {
        sut = new OrderRepositoryImpl(orderJpaRepository, orderMapper);
        orderJpaRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * 固定 UUID でテストデータを挿入するヘルパーメソッド
     * Native SQL を使用して @UuidGenerator をバイパス
     */
    private void insertOrderWithFixedId(UUID id, String orderNumber, String customerName,
                                        BigDecimal totalAmount, OrderStatus status) {
        entityManager.createNativeQuery(
                        "INSERT INTO orders (id, order_number, customer_name, total_amount, status, created_at, updated_at) " +
                                "VALUES (:id, :orderNumber, :customerName, :totalAmount, :status, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .setParameter("id", id)
                .setParameter("orderNumber", orderNumber)
                .setParameter("customerName", customerName)
                .setParameter("totalAmount", totalAmount)
                .setParameter("status", status.name())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testSaveOrder() {
        // Given
        Order order = new Order(
                "TEST-001",
                "テスト太郎",
                10000.00,
                OrderStatus.PENDING
        );

        // When
        Order savedOrder = sut.save(order);

        // Then
        assertThat(savedOrder.orderNumber()).isEqualTo("TEST-001");
        assertThat(savedOrder.customerName()).isEqualTo("テスト太郎");
        assertThat(savedOrder.totalAmount()).isEqualTo(10000.00);
        assertThat(savedOrder.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void testFindByOrderNumber() {
        // Given
        insertOrderWithFixedId(TEST_UUID_2, "TEST-002", "テスト花子",
                new BigDecimal("5000.00"), OrderStatus.CONFIRMED);

        // When
        Optional<Order> found = sut.findByOrderNumber("TEST-002");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().customerName()).isEqualTo("テスト花子");
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
        insertOrderWithFixedId(TEST_UUID_3, "TEST-003", "山田一郎",
                new BigDecimal("3000.00"), OrderStatus.PENDING);
        insertOrderWithFixedId(TEST_UUID_4, "TEST-004", "山田二郎",
                new BigDecimal("4000.00"), OrderStatus.COMPLETED);

        // When
        List<Order> pendingOrders = sut.findByStatus(OrderStatus.PENDING);

        // Then
        assertThat(pendingOrders).hasSize(1);
        assertThat(pendingOrders.get(0).orderNumber()).isEqualTo("TEST-003");
    }

    @Test
    void testFindByCustomerNameContaining() {
        // Given
        insertOrderWithFixedId(TEST_UUID_5, "TEST-005", "鈴木太郎",
                new BigDecimal("1000.00"), OrderStatus.PENDING);
        insertOrderWithFixedId(TEST_UUID_6, "TEST-006", "田中次郎",
                new BigDecimal("2000.00"), OrderStatus.PENDING);

        // When
        List<Order> found = sut.findByCustomerNameContaining("鈴木");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).orderNumber()).isEqualTo("TEST-005");
    }

    @Test
    void testFindAll() {
        // Given
        insertOrderWithFixedId(TEST_UUID_7, "TEST-007", "佐藤三郎",
                new BigDecimal("6000.00"), OrderStatus.SHIPPED);
        insertOrderWithFixedId(TEST_UUID_8, "TEST-008", "佐藤四郎",
                new BigDecimal("7000.00"), OrderStatus.CANCELLED);

        // When
        List<Order> allOrders = sut.findAll();

        // Then
        assertThat(allOrders).hasSize(2);
    }
}
