package backend.order_service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<Item> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Item {
        private String productId;
        private int quantity;
    }
}
