package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired private OrderRepository orderRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private ProductRepository productRepository;

    @Transactional
    public Order createOrder(String userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        double totalAmount = 0;
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            // Check and reduce stock
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if(product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock not available for: " + product.getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            return new OrderItem(cartItem.getProductId(), cartItem.getProductName(), cartItem.getQuantity(), cartItem.getPrice());
        }).collect(Collectors.toList());

        totalAmount = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus("CREATED");
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Clear Cart
        cartRepository.deleteByUserId(userId);

        return savedOrder;
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // Bonus: History
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    // Bonus: Cancel
    @Transactional
    public Order cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order. Status is: " + order.getStatus());
        }

        // Restore Stock
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

    public void updateOrderStatus(String orderId, String status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}