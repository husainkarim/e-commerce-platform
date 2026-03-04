package backend.product_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ordered")
public class Ordered {
    @Id
    private String id;
    private String orderId;
    private String productId;
    private String clientId;
    private String sellerId;
    private int quantity;
    private double price;
}
