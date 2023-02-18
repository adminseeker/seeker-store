package com.adminseeker.inventoryservice.proxies;

import lombok.Data;

@Data
public class ProductResponse {
    Product product;
    Seller seller;
}
