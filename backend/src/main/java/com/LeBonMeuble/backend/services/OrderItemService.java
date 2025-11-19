package com.LeBonMeuble.backend.services;

import com.LeBonMeuble.backend.DTO.OrderItemData;
import com.LeBonMeuble.backend.entities.EntityFurniture;
import com.LeBonMeuble.backend.entities.EntityOrder;
import com.LeBonMeuble.backend.entities.EntityOrderItem;
import com.LeBonMeuble.backend.repositories.FurnitureRepository;
import com.LeBonMeuble.backend.repositories.OrderItemRepository;
import com.LeBonMeuble.backend.repositories.OrderRepository;
import com.LeBonMeuble.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FurnitureRepository furnitureRepository;
    private final UserRepository userRepository;

    public EntityOrder createOrder(Long userId, List<OrderItemData> products) {

        // 1️⃣ créer la commande
        EntityOrder order = new EntityOrder();
        order.setStatus("PAID");
        order.setTotalAmount(
                products.stream().mapToLong(p -> p.getPrice() * p.getQuantity()).sum()
        );
        order.setUser(userRepository.findById(userId).orElseThrow());

        order = orderRepository.save(order);

        // 2️⃣ créer les items
        for (OrderItemData p : products) {
            EntityFurniture furniture = furnitureRepository.findById(p.getFurnitureId())
                    .orElseThrow();

            // (optionnel) changer status du meuble
            furniture.setStatus("sold");
            furnitureRepository.save(furniture);

            EntityOrderItem item = new EntityOrderItem();
            item.setOrder(order);
            item.setFurniture(furniture);
            item.setPrice(p.getPrice());
            item.setQuantity(p.getQuantity());

            orderItemRepository.save(item);
        }

        return order;
    }
}
