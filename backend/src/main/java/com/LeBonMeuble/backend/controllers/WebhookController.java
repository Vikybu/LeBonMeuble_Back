package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.DTO.CartItemDTO;
import com.LeBonMeuble.backend.services.OrderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final OrderService orderService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) throws IOException {

        String payload = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String sigHeader = request.getHeader("Stripe-Signature");

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.out.println(">>> Invalid signature");
            return ResponseEntity.ok("");
        }

        System.out.println(">>> EVENT TYPE = " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                var deserializer = event.getDataObjectDeserializer();

                String rawJson = deserializer.getRawJson();
                Session session = ApiResource.GSON.fromJson(rawJson, Session.class);

                System.out.println(">>> SESSION PARSED OK");
                System.out.println(">>> METADATA = " + session.getMetadata());

                String rawItems = session.getMetadata().get("order_items");
                Long userId = Long.valueOf(session.getMetadata().get("user_id"));

                Type listType = new TypeToken<List<CartItemDTO>>() {
                }.getType();
                List<CartItemDTO> cartItems = new Gson().fromJson(rawItems, listType);

                orderService.createOrder(userId, cartItems);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("ok");
    }
}