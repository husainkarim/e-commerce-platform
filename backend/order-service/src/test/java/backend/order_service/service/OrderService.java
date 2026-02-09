package backend.order_service.service;

import org.springframework.stereotype.Service;

import backend.order_service.model.Order;
import backend.order_service.repository.OrderRepository;


@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        // Business logic to create an order
        // For simplicity, we just save an empty order and notify Kafka
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }
}
