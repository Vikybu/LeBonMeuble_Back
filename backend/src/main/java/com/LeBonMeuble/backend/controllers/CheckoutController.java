package com.LeBonMeuble.backend.controllers;

import com.LeBonMeuble.backend.DTO.CheckoutRequest;
import com.LeBonMeuble.backend.DTO.StripeResponse;
import com.LeBonMeuble.backend.services.StripeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final StripeService stripeService;

    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<StripeResponse> checkout(@RequestBody CheckoutRequest request) {
        StripeResponse response = stripeService.createCheckoutSession(request);
        return ResponseEntity.ok(response);
    }
}
