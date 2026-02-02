package com.example.app.infra.order;

import com.example.app.domain.order.Order;
import com.example.app.domain.order.OrderStatus;
import com.example.app.usecase.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 注文リポジトリ実装
 */
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .flatMap(t -> orderMapper.toDomain(t).stream())
                .toList();
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber)
                .flatMap(orderMapper::toDomain);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatus(status).stream()
                .flatMap(t -> orderMapper.toDomain(t).stream())
                .toList();
    }

    @Override
    public List<Order> findByCustomerNameContaining(String customerName) {
        return orderJpaRepository.findByCustomerNameContaining(customerName).stream()
                .flatMap(t -> orderMapper.toDomain(t).stream())
                .toList();
    }

    @Override
    public Order save(Order order) {
        OrderTable table = orderMapper.toTable(order);
        OrderTable savedTable = orderJpaRepository.save(table);
        return orderMapper
                .toDomain(savedTable)
                .orElseThrow(() -> new IllegalStateException("Failed to map saved OrderTable to Order domain"));
    }

    @Override
    public void deleteAll() {
        orderJpaRepository.deleteAll();
    }
}
