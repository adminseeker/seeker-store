package com.adminseeker.inventoryservice.proxies;

import java.util.List;

import lombok.Data;

@Data
public class Product {
    Long productId;
    String name;
    String skucode;
    Long sellerId;
    List<Variant> variants;
}
