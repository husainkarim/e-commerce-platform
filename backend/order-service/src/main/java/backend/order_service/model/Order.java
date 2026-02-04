package backend.order_service.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    private String sellerId;

    @NotBlank(message = "Order must contain at least one item")
    @Valid // This is crucial to trigger validation on the OrderItem list
    private List<OrderItem> items;

    @Pattern(regexp = "PENDING|CONFIRMED|PROCESSED|DELIVERED|CANCELLED", message = "Invalid status")
    private String status = "PENDING";

    @NotBlank(message = "Payment method is required")
    private String paymentMethod = "PAY ON DELIVERY";

    @NotNull(message = "Shipping address cannot be null")
    @Valid // Triggers validation on the ShippingAddress inner class
    private ShippingAddress shippingAddress;

    @PositiveOrZero(message = "Total price cannot be negative")
    private double totalAmount;

    @PastOrPresent(message = "Creation date cannot be in the future")
    private Date createdAt = new Date();

    private Date updatedAt = new Date();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        @NotBlank(message = "Product ID is required")
        private String productId;

        @NotBlank(message = "Product name is required")
        private String productName;

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
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Invalid email format")
        private String email;

        @NotBlank(message = "Phone number is required")
        private String phone;
        
        @NotBlank(message = "Address is required")
        private String address;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "Country is required")
        private String country;

        private String notes;
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity())
            .sum();
    }

    public Boolean hasMultipleSellers() {
        return items.stream()
            .map(OrderItem::getSellerId)
            .distinct()
            .count() > 1;
    }

    public List<String> getSellersIds() {
        return items.stream()
            .map(OrderItem::getSellerId)
            .distinct()
            .toList();
    }

    public Order createOrderForSeller(String sellerId) {
        List<OrderItem> sellerItems = items.stream()
            .filter(i -> i.getSellerId().equals(sellerId))
            .toList();
        Order sellerOrder = new Order();
        sellerOrder.setUserId(this.userId);
        sellerOrder.setSellerId(sellerId);
        sellerOrder.setItems(sellerItems);
        sellerOrder.setShippingAddress(this.shippingAddress);
        sellerOrder.setPaymentMethod(this.paymentMethod);
        sellerOrder.calculateTotal();
        return sellerOrder;
    }
}