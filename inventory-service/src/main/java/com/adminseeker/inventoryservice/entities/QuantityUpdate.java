package com.adminseeker.inventoryservice.entities;

import lombok.Data;

@Data
public class QuantityUpdate {
    private String productSkucode;
    private String variantSkucode;
    private Integer quantity;
}
