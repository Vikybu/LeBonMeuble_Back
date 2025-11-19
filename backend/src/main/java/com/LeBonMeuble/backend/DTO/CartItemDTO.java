package com.LeBonMeuble.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long furnitureId;
    private String name;
    private Long amount;
    private int quantity;
}
