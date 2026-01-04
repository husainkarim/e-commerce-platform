package backend.order_service.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    @NotBlank
    private String userId;
    private List<Item> items;
    private String status;
    private ShippingAddress shippingAddress;
    private double totalPrice;
    private Date createdAt;
    private Date processedAt;
    private Date shippedAt;
    private Date deliveredAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class Item {
        private String productId;
        private int quantity;
        private double price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class ShippingAddress {
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
    }
}