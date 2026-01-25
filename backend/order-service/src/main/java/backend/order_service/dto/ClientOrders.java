package backend.order_service.dto;

import java.util.List;

import backend.order_service.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientOrders {
    private String clientId;
    private List<Order> orders;
    private int totalOrders;
    private double totalSpent;
}
