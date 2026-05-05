package backend.order_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.ProductAllowed;

@Repository
public interface ProductAllowedRepository extends MongoRepository<ProductAllowed, String> {
    List<ProductAllowed> findByProductId(String productId);
    List<ProductAllowed> findByProductIdIn(List<String> productIds);
}
