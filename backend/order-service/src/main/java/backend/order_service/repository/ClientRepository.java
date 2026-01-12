package backend.order_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import backend.order_service.model.Client;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
    Optional<Client> findByUserId(String userId);
}
