package com.adminseeker.productservice.entities;

import com.adminseeker.productservice.proxies.User;

import lombok.Data;

@Data
public class ProductResponse {
    
    Product product;
    User seller;

}
