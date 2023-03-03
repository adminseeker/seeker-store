package com.adminseeker.orderservice.proxies;

import lombok.Data;

@Data
public class QuantityUpdate {
    private String productSkucode;
    private String variantSkucode;
    private Integer quantity;
    private Long productId;
    private Long variantId;
}
