package com.adminseeker.inventoryservice.entities;

import com.adminseeker.inventoryservice.proxies.ProductResponse;

import lombok.Data;

@Data
public class InventoryResponse {
    
    Inventory inventory;
    ProductResponse productResponse;

}
