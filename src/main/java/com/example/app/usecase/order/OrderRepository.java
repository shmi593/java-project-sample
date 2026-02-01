package com.example.app.usecase.order;

import com.example.app.domain.order.OrderStatus;
import com.example.app.infra.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 注文リポジトリ
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

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
}
