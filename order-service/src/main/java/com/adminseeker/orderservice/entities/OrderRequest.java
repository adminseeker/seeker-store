package com.adminseeker.orderservice.entities;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private String orderId;
    private String itemId;
}
