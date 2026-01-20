package com.example.ecommerce.repository;

import com.example.ecommerce.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Payment findByOrderId(String orderId);
}