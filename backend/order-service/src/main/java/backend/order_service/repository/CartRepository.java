package backend.order_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.Cart;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    List<Cart> findByUserId(String userId);
}
