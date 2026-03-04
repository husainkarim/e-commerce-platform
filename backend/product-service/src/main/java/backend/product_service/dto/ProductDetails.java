package backend.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetails {
    private String id;
    private String name;
    private String description;
    private String category;
    private double price;
    private double revenue;
    private int quantity;
    private int unitsSold;
    private String userId;
}
