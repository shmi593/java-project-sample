-- Insert sample data
INSERT INTO orders (order_number, customer_name, total_amount, status, created_at, updated_at) VALUES
('ORD-001', '山田太郎', 15000.00, 'COMPLETED', '2025-12-01 10:00:00', '2025-12-01 10:00:00'),
('ORD-002', '鈴木花子', 8500.50, 'PENDING', '2025-12-10 14:30:00', '2025-12-10 14:30:00'),
('ORD-003', '佐藤次郎', 32000.00, 'SHIPPED', '2025-12-15 09:15:00', '2025-12-16 11:00:00');
