package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.DTO.CartItemDTO;
import com.LeBonMeuble.backend.entities.EntityFurniture;
import com.LeBonMeuble.backend.entities.EntityOrder;
import com.LeBonMeuble.backend.entities.EntityOrderItem;
import com.LeBonMeuble.backend.entities.EntityUser;
import com.LeBonMeuble.backend.repositories.FurnitureRepository;
import com.LeBonMeuble.backend.repositories.OrderItemRepository;
import com.LeBonMeuble.backend.repositories.OrderRepository;
import com.LeBonMeuble.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FurnitureRepository furnitureRepository;
    private final UserRepository userRepository;

    @Transactional
    public EntityOrder createOrder(Long userId, List<CartItemDTO> items) {

        // 1️⃣ Récupérer l'utilisateur
        EntityUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 2️⃣ Créer la commande
        EntityOrder order = new EntityOrder();
        order.setStatus("PAID");

        long totalAmount = items.stream()
                .mapToLong(CartItemDTO::getAmount)
                .sum();

        order.setTotalAmount(totalAmount);
        order.setUser(user);

        order = orderRepository.save(order);

        // 3️⃣ Ajouter les items
        for (CartItemDTO cartItem : items) {

            EntityFurniture furniture = furnitureRepository.findById(cartItem.getFurnitureId())
                    .orElseThrow(() -> new RuntimeException("Furniture introuvable"));

            // Marquer comme vendu
            furniture.setStatus("SOLD");
            furnitureRepository.save(furniture);

            EntityOrderItem item = new EntityOrderItem();
            item.setOrder(order);
            item.setFurniture(furniture);     // ➜ Relation JPA correcte
            item.setPrice(cartItem.getAmount());
            item.setQuantity(cartItem.getQuantity());

            orderItemRepository.save(item);
        }

        return order;
    }
}
