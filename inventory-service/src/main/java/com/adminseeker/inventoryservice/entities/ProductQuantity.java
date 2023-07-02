package com.adminseeker.inventoryservice.entities;

import lombok.Data;

@Data
public class ProductQuantity {
    private String productSkucode;
    private Integer quantity;
}
