package backend.product_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDashboard {
    List<ProductDetails> products;
    int totalUnitsSold;
    double totalRevenue;

    public SellerDashboard(List<ProductDetails> products) {
        this.products = products;
        this.totalUnitsSold = products.stream().mapToInt(ProductDetails::getUnitsSold).sum();
        this.totalRevenue = products.stream().mapToDouble(ProductDetails::getRevenue).sum();
    }
}
