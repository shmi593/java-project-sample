package com.example.app.infra.order;

import com.example.app.domain.order.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 注文マッパー
 * OrderTable（インフラ層）とOrder（ドメイン層）の相互変換を行う
 */
@Component
public class OrderMapper {

    /**
     * OrderTable から Order ドメインへ変換
     */
    public Optional<Order> toDomain(OrderTable table) {
        return Optional.ofNullable(table)
                .map(t -> new Order(
                        t.getOrderNumber(),
                        t.getCustomerName(),
                        Optional.ofNullable(t.getTotalAmount()).orElseGet(() -> BigDecimal.valueOf(0)).doubleValue(),
                        t.getStatus()
                ));
    }

    /**
     * Order ドメインから OrderTable へ変換
     * 新規作成時に使用（IDなし）
     */
    public OrderTable toTable(Order order) {
        if (order == null) {
            return null;
        }
        return OrderTable.builder()
                .orderNumber(order.orderNumber())
                .customerName(order.customerName())
                .totalAmount(order.totalAmount() != null ? BigDecimal.valueOf(order.totalAmount()) : null)
                .status(order.status())
                .build();
    }

    /**
     * Order ドメインから既存の OrderTable を更新
     */
    public OrderTable updateTable(OrderTable existingTable, Order order) {
        if (existingTable == null || order == null) {
            return existingTable;
        }
        existingTable.setOrderNumber(order.orderNumber());
        existingTable.setCustomerName(order.customerName());
        existingTable.setTotalAmount(order.totalAmount() != null ? BigDecimal.valueOf(order.totalAmount()) : null);
        existingTable.setStatus(order.status());
        return existingTable;
    }
}
