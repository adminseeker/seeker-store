package com.adminseeker.orderservice.proxies;

import java.util.List;

import lombok.Data;

@Data
public class Product {
    private Long productId;
    private String name;
    private String description;
    private Long sellerId;
    private String skucode;
    private Double price;
    private List<Variant> variants;
}
