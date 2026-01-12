package backend.product_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.product_service.model.Ordered;

@Repository
public interface OrderedRepository extends MongoRepository<Ordered, String> {
    List<Ordered> findByOrderId(String orderId);
    List<Ordered> findBySellerId(String sellerId);
    List<Ordered> findByClientId(String clientId);
    List<Ordered> findByProductId(String productId);
}
