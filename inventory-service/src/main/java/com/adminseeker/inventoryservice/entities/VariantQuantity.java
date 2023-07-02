package com.adminseeker.inventoryservice.entities;

import lombok.Data;

@Data
public class VariantQuantity {
    private String productSkucode;
    private String variantSkucode;
    private Integer quantity;
}
