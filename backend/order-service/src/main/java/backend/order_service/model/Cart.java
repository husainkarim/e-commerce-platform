package backend.order_service.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotBlank(message = "User ID is required to maintain a cart")
    @Indexed(unique = true)
    private String userId;

    @NotEmpty(message = "Cart cannot be empty")
    @Valid
    private List<CartItem> items;

    public Cart(String userId) {
        this.userId = userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItem {        
        @NotBlank(message = "Product ID is required")
        private String productId;

        @NotBlank(message = "Seller ID is required")
        private String sellerId;

        private String productName; // Optional: stored for faster frontend rendering

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        @Min(value = 0)
        private double price; // Helpful for showing a "Running Total" in the UI
    }
}