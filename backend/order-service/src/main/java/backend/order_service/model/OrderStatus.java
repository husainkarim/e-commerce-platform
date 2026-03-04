package backend.order_service.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_statuses")
public class OrderStatus {
    @Id
    private String id;
    private String orderId;
    private String status;
    private Date updatedAt = new Date();
    private Boolean completed = false;
}
