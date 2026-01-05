package backend.order_service.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;

import backend.order_service.repository.OrderRepository;
import backend.order_service.repository.CartRepository;
import backend.order_service.repository.OrderStatusRepository;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import backend.order_service.model.Cart;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderStatusRepository orderStatusRepository;

    public OrderController(OrderRepository orderRepository, CartRepository cartRepository, OrderStatusRepository orderStatusRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.orderStatusRepository = orderStatusRepository;
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

    //TODO: get Order by ID

    //TODO: update Order Status

    //TODO: get Orders by User ID

    //TODO: get Orders by Seller ID

    //TODO: delete Order

}
