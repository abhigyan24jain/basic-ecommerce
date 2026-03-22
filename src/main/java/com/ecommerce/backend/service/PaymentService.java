package com.ecommerce.backend.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils; // ADDED MISSING IMPORT
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public String createRazorpayOrder(BigDecimal amount, String receiptId) {
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            BigDecimal amountInPaise = amount.multiply(new BigDecimal("100"));

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise.intValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receiptId);

            Order order = razorpay.orders.create(orderRequest);

            return order.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate Razorpay payment: " + e.getMessage());
        }
    }

    // THIS IS THE METHOD THAT WAS MISSING!
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            // This method validates the cryptographic signature using your secret key
            return Utils.verifyPaymentSignature(options, razorpayKeySecret);
        } catch (Exception e) {
            return false;
        }
    }
}