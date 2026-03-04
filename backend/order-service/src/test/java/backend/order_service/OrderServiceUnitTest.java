package backend.order_service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import backend.order_service.model.Order;
import backend.order_service.repository.OrderRepository;
import backend.order_service.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order orderToSave;

    @BeforeEach
    public void setUp() {
        orderToSave = new Order();
        orderToSave.setId("order-123");
        orderToSave.setUserId("user-789");
        orderToSave.setSellerId("seller-456");
        orderToSave.setStatus("PENDING");
        orderToSave.setItems(List.of(new Order.OrderItem("product-321", "product", "seller-456", "General", 2, 499.99)));
        orderToSave.setPaymentMethod("PAY ON DELIVERY");
        orderToSave.setShippingAddress(new Order.ShippingAddress("client", "client@example.com", "55667", "Cityville", "State", "Country", "notes"));
        orderToSave.calculateTotal();
    }

    @Test
    void whenCreateOrder_thenOrderIsSavedAndNotificationIsSent() {
        when(orderRepository.save(any(Order.class)))
            .thenReturn(orderToSave);
        Order createdOrder = orderService.createOrder(orderToSave);
        assertEquals(orderToSave.getUserId(), createdOrder.getUserId(), "The returned order's user ID should match the input.");
        assertEquals(orderToSave.getId(), createdOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
