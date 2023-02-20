package com.adminseeker.cartservice.entities;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Item item;    
}
