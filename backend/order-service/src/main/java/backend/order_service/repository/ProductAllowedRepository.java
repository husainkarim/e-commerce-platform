package backend.order_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.ProductAllowed;

@Repository
public interface ProductAllowedRepository extends MongoRepository<ProductAllowed, String> {
    ProductAllowed findByProductId(String productId);
}
