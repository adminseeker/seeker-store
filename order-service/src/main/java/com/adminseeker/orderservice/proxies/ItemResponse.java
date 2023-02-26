package com.adminseeker.orderservice.proxies;

import lombok.Data;

@Data
public class ItemResponse {
    private Long productId;
    private String itemId;
    private String name;
    private String description;
    private Long sellerId;
    private String skucode;
    private Double price;
    private Variant variant;
    private Integer quantity;
}
