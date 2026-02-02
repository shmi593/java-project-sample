package com.example.app.usecase.order;

import com.example.app.domain.order.Order;
import com.example.app.domain.order.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * 注文リポジトリ
 */
public interface OrderRepository {

    /**
     * 全注文を取得
     */
    List<Order> findAll();

    /**
     * 注文番号で検索
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * ステータスで検索
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * 顧客名で検索（部分一致）
     */
    List<Order> findByCustomerNameContaining(String customerName);

    /**
     * 注文を保存
     */
    Order save(Order order);

    /**
     * 全削除
     */
    void deleteAll();
}
