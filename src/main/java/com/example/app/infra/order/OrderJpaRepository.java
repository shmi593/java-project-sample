package com.example.app.infra.order;

import com.example.app.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 注文JPAリポジトリ
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderTable, UUID> {

    /**
     * 注文番号で検索
     */
    Optional<OrderTable> findByOrderNumber(String orderNumber);

    /**
     * ステータスで検索
     */
    List<OrderTable> findByStatus(OrderStatus status);

    /**
     * 顧客名で検索（部分一致）
     */
    List<OrderTable> findByCustomerNameContaining(String customerName);
}
