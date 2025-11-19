package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.DTO.CartItemDTO;
import com.LeBonMeuble.backend.DTO.CheckoutRequest;
import com.LeBonMeuble.backend.DTO.StripeResponse;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse createCheckoutSession(CheckoutRequest request) {

        Stripe.apiKey = secretKey;

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();

        for (CartItemDTO item : request.getItems()) {

            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L) // un meuble unique
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setUnitAmount(item.getAmount())  // amount déjà en centimes
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder()
                                                            .setName(item.getName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            lineItems.add(lineItem);
        }

        // envoyer les items en metadata pour le webhook
        String metadataJson = new Gson().toJson(request.getItems());

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:5173/payment-success")
                        .setCancelUrl("http://localhost:5173/payment-cancel")
                        .putMetadata("order_items", metadataJson)
                        .putMetadata("user_id", request.getUserId().toString())
                        .addAllLineItem(lineItems)
                        .build();

        try {
            Session session = Session.create(params);

            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Checkout session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();

        } catch (StripeException e) {
            throw new RuntimeException("Erreur Stripe : " + e.getMessage());
        }
    }
}
