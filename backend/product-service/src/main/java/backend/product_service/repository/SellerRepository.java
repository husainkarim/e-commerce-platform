package backend.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.product_service.model.Seller;

@Repository
public interface SellerRepository extends MongoRepository<Seller, String> {
    // find seller by email
    Seller findByEmail(String email);
    // find seller by userId
    Seller findByUserId(String userId);
}
