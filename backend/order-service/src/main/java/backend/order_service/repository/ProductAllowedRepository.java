package backend.order_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.ProductAllowed;

@Repository
public interface ProductAllowedRepository extends MongoRepository<ProductAllowed, String> {
    Optional<ProductAllowed> findByProductId(String productId);
}
