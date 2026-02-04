package backend.order_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.OrderStatus;

@Repository
public interface OrderStatusRepository extends MongoRepository<OrderStatus, String> {
    Optional<OrderStatus> findByOrderId(String orderId);
}
