package com.example.ecommerce.service;

import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentService {
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private RestTemplate restTemplate;

    public Payment initiatePayment(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Async Simulation of Payment Gateway
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000); // Wait 3 seconds

                // Trigger Webhook
                String webhookUrl = "http://localhost:8080/api/webhooks/payment";
                PaymentWebhookRequest webhookRequest = new PaymentWebhookRequest();
                webhookRequest.setOrderId(request.getOrderId());
                webhookRequest.setPaymentId(savedPayment.getId());
                webhookRequest.setStatus("SUCCESS"); // Simulating success

                restTemplate.postForEntity(webhookUrl, webhookRequest, String.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return savedPayment;
    }

    public void updatePaymentStatus(String paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow();
        payment.setStatus(status);
        paymentRepository.save(payment);
    }
}