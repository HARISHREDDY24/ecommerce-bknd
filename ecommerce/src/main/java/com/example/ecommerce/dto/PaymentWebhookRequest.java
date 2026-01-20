package com.example.ecommerce.dto;
import lombok.Data;

@Data
public class PaymentWebhookRequest {
    private String orderId;
    private String paymentId;
    private String status;
}