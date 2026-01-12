package backend.order_service.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid // This is crucial to trigger validation on the OrderItem list
    private List<OrderItem> items;

    @Pattern(regexp = "PENDING|SHIPPED|DELIVERED|CANCELLED", message = "Invalid status")
    private String status = "PENDING";

    @NotBlank(message = "Payment method is required")
    private String paymentMethod = "PAY_ON_DELIVERY";

    @NotNull(message = "Shipping address cannot be null")
    @Valid // Triggers validation on the ShippingAddress inner class
    private ShippingAddress shippingAddress;

    @PositiveOrZero(message = "Total price cannot be negative")
    private double totalPrice;

    @PastOrPresent(message = "Creation date cannot be in the future")
    private Date createdAt = new Date();

    private Date updatedAt = new Date();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        @NotBlank(message = "Product ID is required")
        private String productId;

        @NotBlank(message = "Seller ID is required")
        private String sellerId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;

        @Positive(message = "Price must be greater than zero")
        private double price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingAddress {
        @NotBlank(message = "Street is required")
        private String street;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Zip code is required")
        @Size(min = 5, max = 10, message = "Zip code must be between 5 and 10 characters")
        private String zipCode;

        @NotBlank(message = "Country is required")
        private String country;
    }

    public void calculateTotal() {
        this.totalPrice = items.stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity())
            .sum();
    }
}