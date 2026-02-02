package com.example.app.controller.order;

import com.example.app.domain.order.Order;
import com.example.app.domain.order.OrderStatus;
import com.example.app.usecase.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 注文コントローラー
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    static final String API_PATH = "/orders";

    private final OrderRepository orderRepository;

    /**
     * 注文一覧取得
     */
    @GetMapping(API_PATH)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * 注文詳細取得
     */
    @GetMapping(API_PATH + "/{orderNumber}")
    public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 注文作成
     */
    @PostMapping(API_PATH)
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        String orderNumber = generateOrderNumber();

        Order order = new Order(
                orderNumber,
                request.getCustomerName(),
                request.getTotalAmount().doubleValue(),
                OrderStatus.PENDING
        );

        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    /**
     * 注文番号生成
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "ORD-" + timestamp;
    }
}
