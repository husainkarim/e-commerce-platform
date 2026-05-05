package backend.order_service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.order_service.dto.ClientOrders;
import backend.order_service.dto.UpdateState;
import backend.order_service.model.Cart;
import backend.order_service.model.Client;
import backend.order_service.model.Order;
import backend.order_service.model.OrderStatus;
import backend.order_service.model.ProductAllowed;
import backend.order_service.repository.CartRepository;
import backend.order_service.repository.ClientRepository;
import backend.order_service.repository.OrderRepository;
import backend.order_service.repository.OrderStatusRepository;
import backend.order_service.repository.ProductAllowedRepository;
import backend.order_service.service.KafkaService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final KafkaService kafkaService;
    private final ClientRepository clientRepository;
    private final ProductAllowedRepository productAllowedRepository;

    private static final String[] CATEGORIES = {
        "General", "Electronics", "Fashion", "Home", "Sports", "Books", "Toys",
        "Health", "Automotive", "Garden", "Music", "Movies", "Groceries",
        "Jewelry", "Beauty"
    };

    public OrderController(OrderRepository orderRepository, CartRepository cartRepository, OrderStatusRepository orderStatusRepository, KafkaService kafkaService, ClientRepository clientRepository, ProductAllowedRepository productAllowedRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.kafkaService = kafkaService;
        this.clientRepository = clientRepository;
        this.productAllowedRepository = productAllowedRepository;
    }
    
    //update Cart
    @PutMapping("/update-cart")
    public ResponseEntity<Map<String, Object>> updateCart(@RequestParam String userId, @RequestBody Cart updatedCart) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Received cart update for userId: " + userId + " with items: " + updatedCart.getItems());
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userId.equals(updatedCart.getUserId())) {
            response.put("message", "User ID mismatch");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // check items categories is it valid or not
        for (Cart.CartItem item : updatedCart.getItems()) {
            if (!List.of(CATEGORIES).contains(item.getCategory())) {
                response.put("message", "Invalid category: " + item.getCategory());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        
        Optional<Cart> existingCarts = cartRepository.findByUserId(userId);
        Cart existingCart;
        if (existingCarts.isEmpty()) {
            existingCart = new Cart(userId);
        } else {
            existingCart = existingCarts.get();
        }
        existingCart.setItems(updatedCart.getItems());
        
        List<String> productIds = existingCart.getItems()
            .stream()
            .map(Cart.CartItem::getProductId)
            .toList();

        List<ProductAllowed> allowedProducts = productAllowedRepository.findByProductIdIn(productIds);

        Set<String> allowedIds = allowedProducts.stream()
            .map(ProductAllowed::getProductId)
            .collect(Collectors.toSet());

        existingCart.getItems().removeIf(c ->
            !allowedIds.contains(c.getProductId())
        );
        
        // save the cleaned cart
        cartRepository.save(existingCart);
        response.put("cart", existingCart);
        response.put("message", "Cart updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get Cart by User ID
    @GetMapping("/cart")
    public ResponseEntity<Map<String, Object>> getCartByUserId(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Optional<Cart> carts = cartRepository.findByUserId(userId);
        Cart cart;
        if (carts.isEmpty()) {
            cart = new Cart(userId);
        } else {
            cart = carts.get();
        }

        // check for if one of the carts is null or has been deleted
        cart.getItems().removeIf(c ->
            productAllowedRepository.findByProductId(c.getProductId()).isEmpty()
        );
        // save the cleaned cart
        cartRepository.save(cart);
        response.put("cart", cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // place Order TODO: connect to frontend
    @PostMapping("/place-order")
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestParam String userId, @RequestBody Order order) {
        Map<String, Object> response = new HashMap<>();
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!userId.equals(order.getUserId())) {
            response.put("message", "User ID mismatch");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            response.put("message", "Order must contain at least one item");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // check items categories is it valid or not
        for (Order.OrderItem item : order.getItems()) {
            if (!List.of(CATEGORIES).contains(item.getCategory())) {
                response.put("message", "Invalid category: " + item.getCategory());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        
        // get sellers involved in this order which ensures one order per seller
        List<String> sellerOrders = order.getSellersIds();
        for (String sellerId : sellerOrders) {
            Order sellerOrder = order.createOrderForSeller(sellerId);
            // id is null here and will be generated in the save() method
            sellerOrder = orderRepository.save(sellerOrder);
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOrderId(sellerOrder.getId());
            orderStatus.setStatus(sellerOrder.getStatus());
            orderStatusRepository.save(orderStatus);
            kafkaService.sendOrderCreatedEvent(sellerOrder);
        }
        // clear the cart after placing the order
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            cart.getItems().clear();
            cartRepository.save(cart);
        }
        response.put("message", "Order placed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get Order by ID TODO: connect to frontend
    @GetMapping("/order-details")
    public ResponseEntity<Map<String, Object>> getOrderById(@RequestParam String orderId, @RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        var orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            response.put("order", orderOpt.get());
            var orderStatusList = orderStatusRepository.findByOrderId(orderId);
            if (!orderStatusList.isEmpty()) {
                response.put("status", orderStatusList);
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
    @PostMapping("/update-order-status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestBody UpdateState updateState, @RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        Order orderCheck = orderRepository.findById(updateState.getOrderId()).orElse(null);
        if (orderCheck == null) {
            response.put("message", "Order not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        if (orderCheck.getSellerId() == null || !orderCheck.getSellerId().equals(userId)) {
            response.put("message", "Unauthorized: You are not the seller of this order");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        List<String> validStatuses = List.of("PENDING", "CONFIRMED", "PROCESSED", "DELIVERED", "CANCELLED", "DELETED");
        if (!validStatuses.contains(updateState.getStatus())) {
            response.put("message", "Invalid status: " + updateState.getStatus());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(updateState.getOrderId());
        orderStatus.setStatus(updateState.getStatus());
        // insert new status record
        orderStatusRepository.save(orderStatus);
        // update order record
        if (orderRepository.existsById(updateState.getOrderId())) {
            var orderOpt = orderRepository.findById(updateState.getOrderId());
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setStatus(updateState.getStatus());
                orderRepository.save(order);
            }
        }
        // send kafka event if order is cancelled or deleted
        if (updateState.getStatus().equals("CANCELLED") || updateState.getStatus().equals("DELETED")) {
            var orderOpt = orderRepository.findById(updateState.getOrderId());
            if (orderOpt.isPresent() && updateState.getStatus().equals("CANCELLED")) {
                Order order = orderOpt.get();
                order.setStatus(updateState.getStatus());
                orderRepository.save(order);
                kafkaService.sendOrderDeletedEvent(order);
            } else if (orderOpt.isPresent() && updateState.getStatus().equals("DELETED")) {
                Order order = orderOpt.get();
                orderRepository.deleteById(updateState.getOrderId());
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
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        List<Order> orders = orderRepository.findByUserId(userId);
        response.put("orders", orders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/seller-orders")
    public ResponseEntity<Map<String, Object>> getOrdersBySellerId(@RequestParam String sellerId) {
        Map<String, Object> response = new HashMap<>();
        List<Order> orders = orderRepository.findBySellerId(sellerId);
        response.put("orders", orders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get ordars data for the dashboard
    @GetMapping("/client-dashboard-data")
    public ResponseEntity<Map<String, Object>> getClientDashboardData(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        var orders = orderRepository.findByUserId(userId);
        // if the order status is cancelled or deleted we will not count it in the total spent and total orders
        // copy the orders list becuase the removeIf method will modify the original list and we need to keep it for the client orders response
        List<Order> filteredOrders = new ArrayList<>(orders);
        filteredOrders.removeIf(order -> order.getStatus().equals("CANCELLED") || order.getStatus().equals("DELETED"));
        int totalOrders = filteredOrders.size();
        double totalSpent = filteredOrders.stream().mapToDouble(order -> order.getTotalAmount()).sum();
        ClientOrders clientOrders = new ClientOrders(userId, orders, totalOrders, totalSpent);
        response.put("clientOrders", clientOrders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //cancel Order TODO: connect to frontend
    @DeleteMapping("/delete-order")
    public ResponseEntity<Map<String, Object>> deleteOrder(@RequestParam String orderId, @RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<Client> clients = this.clientRepository.findAll();
        // Check if userId belongs to a valid client
        boolean isValidClient = clients.stream().anyMatch(client -> client.getUserId().equals(userId));
        if (!isValidClient) {
            response.put("message", "Invalid client user ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (orderRepository.existsById(orderId)) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                kafkaService.sendOrderDeletedEvent(order);
            }
            orderRepository.deleteById(orderId);
            
            response.put("message", "Order deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Order not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
