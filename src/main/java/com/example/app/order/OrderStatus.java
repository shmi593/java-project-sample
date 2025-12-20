package com.example.app.order;

/**
 * 注文ステータス
 */
public enum OrderStatus {
    PENDING,    // 保留中
    CONFIRMED,  // 確定
    SHIPPED,    // 発送済み
    COMPLETED,  // 完了
    CANCELLED   // キャンセル
}
