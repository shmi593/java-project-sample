package com.example.app.domain.order;

/**
 * 注文エンティティ
 */
public record Order(String orderNumber, String customerName, Double totalAmount, OrderStatus status) {
}