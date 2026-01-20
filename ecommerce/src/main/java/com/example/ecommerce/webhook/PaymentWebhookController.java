package com.example.ecommerce.webhook;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
public class PaymentWebhookController {
    @Autowired private OrderService orderService;
    @Autowired private PaymentService paymentService;

    @PostMapping("/payment")
    public void handlePaymentWebhook(@RequestBody PaymentWebhookRequest request) {
        System.out.println("Webhook Received: " + request);

        // Update Payment Status
        paymentService.updatePaymentStatus(request.getPaymentId(), request.getStatus());

        // Update Order Status
        if ("SUCCESS".equals(request.getStatus())) {
            orderService.updateOrderStatus(request.getOrderId(), "PAID");
        } else {
            orderService.updateOrderStatus(request.getOrderId(), "PAYMENT_FAILED");
        }
    }
}