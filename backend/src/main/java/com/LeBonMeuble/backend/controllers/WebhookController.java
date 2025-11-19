package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.DTO.CartItemDTO;
import com.LeBonMeuble.backend.services.OrderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.Type;
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
    public String handleStripeWebhook(HttpServletRequest request)
            throws IOException {

        String payload = request.getReader().lines().collect(Collectors.joining());
        String sigHeader = request.getHeader("Stripe-Signature");

        System.out.println(">>> SIG HEADER RECU = " + sigHeader);
        System.out.println(">>> PAYLOAD = " + payload);

        if (sigHeader == null) {
            // Ne jamais throw → renvoie 200 sinon Stripe va spammer !
            System.out.println(">>> ERROR: Missing Stripe-Signature");
            return "";
        }

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            System.out.println(">>> WEBHOOK SECRET UTILISÉ PAR SPRING = " + webhookSecret);

        } catch (SignatureVerificationException e) {
            // Très important : NE PAS renvoyer une erreur 400 !
            System.out.println(">>> ERROR: signature invalide");
            return "";
        }

        System.out.println(">>> EVENT TYPE = " + event.getType());

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                System.out.println(">>> CHECKOUT.SESSION.COMPLETED RECU");

                Session session = (Session) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalStateException("Unable to deserialize event"));

                System.out.println(">>> METADATA = " + session.getMetadata());

                String rawItems = session.getMetadata().get("order_items");
                System.out.println(">>> RAW ITEMS = " + rawItems);

                Long userId = Long.valueOf(session.getMetadata().get("user_id"));
                System.out.println(">>> USER ID = " + userId);

                Type listType = new TypeToken<List<CartItemDTO>>() {}.getType();
                List<CartItemDTO> cartItems = new Gson().fromJson(rawItems, listType);

                System.out.println(">>> ITEMS DESERIALIZED = " + cartItems);

                System.out.println(">>> CREATION ORDER ...");
                orderService.createOrder(userId, cartItems);

                System.out.println(">>> ORDER CREATED OK !");

            } catch (Exception e) {
                System.out.println(">>> ERROR IN HANDLER");
                e.printStackTrace();
            }

        }

        return "ok";
    }
}