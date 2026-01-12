package backend.order_service.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.order_service.dto.UpdateState;
import backend.order_service.model.Cart;
import backend.order_service.model.Order;
import backend.order_service.model.OrderStatus;
import backend.order_service.repository.CartRepository;
import backend.order_service.repository.OrderRepository;
import backend.order_service.repository.OrderStatusRepository;
import backend.order_service.service.KafkaService;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final KafkaService kafkaService;

    public OrderController(OrderRepository orderRepository, CartRepository cartRepository, OrderStatusRepository orderStatusRepository, KafkaService kafkaService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.kafkaService = kafkaService;
    }
    
    //update Cart
    @PutMapping("/update-cart")
    public ResponseEntity<Map<String, Object>> updateCart(@RequestParam String userId, @RequestBody Cart updatedCart) {
        Map<String, Object> response = new HashMap<>();
        if (!userId.equals(updatedCart.getUserId())) {
            response.put("message", "User ID mismatch");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        cartRepository.save(updatedCart);
        response.put("cart", updatedCart);
        response.put("message", "Cart updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get Cart by User ID
    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCartByUserId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        Optional<Cart> IsThereCart = cartRepository.findByUserId(userId);
        Cart cart = new Cart();
        if (IsThereCart.isEmpty()) {
            cart.setUserId(userId);
            cartRepository.save(cart);
        } else {
            cart = IsThereCart.get();
        }
        response.put("cart", cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    //TODO: place Order
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestParam String userId, @RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        
        if (!userId.equals(order.getUserId())) {
            response.put("message", "User ID mismatch");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        order.calculateTotal();
        orderRepository.save(order);
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(order.getId());
        orderStatus.setStatus(order.getStatus());
        orderStatusRepository.save(orderStatus);
        kafkaService.sendOrderCreatedEvent(order);
        response.put("order", order);
        response.put("message", "Order placed successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get Order by ID TODO: connect to frontend
    @GetMapping("/order")
    public ResponseEntity<Map<String, Object>> getOrderById(@RequestParam String orderId) {
        Map<String, Object> response = new HashMap<>();
        var orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            response.put("order", orderOpt.get());
            var orderStatusOpt = orderStatusRepository.findById(orderId);
            if (orderStatusOpt.isPresent()) {
                response.put("status", orderStatusOpt.get().getStatus());
            } else {
                response.put("status", "Status not found");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Order not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    //TODO: update Order Status
    @PutMapping("/update-order-status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestBody UpdateState updateState) {
        Map<String, Object> response = new HashMap<>();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(updateState.getOrderId());
        orderStatus.setStatus(updateState.getStatus());
        orderStatusRepository.save(orderStatus);
        if (orderRepository.existsById(updateState.getOrderId())) {
            var orderOpt = orderRepository.findById(updateState.getOrderId());
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(updateState.getStatus());
                orderRepository.save(order);
            }
        }
        if (updateState.getStatus().equals("cancelled") || updateState.getStatus().equals("deleted")) {
            var orderOpt = orderRepository.findById(updateState.getOrderId());
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                kafkaService.sendOrderDeletedEvent(order);
            }
        }
        response.put("message", "Order status updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //get Orders by User ID TODO: connect to frontend
    @GetMapping("/user-orders")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        var orders = orderRepository.findByUserId(userId);
        response.put("orders", orders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //cancel Order TODO: connect to frontend
    @DeleteMapping("/delete-order")
    public ResponseEntity<Map<String, Object>> deleteOrder(@RequestParam String orderId) {
        Map<String, Object> response = new HashMap<>();
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            response.put("message", "Order deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Order not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
