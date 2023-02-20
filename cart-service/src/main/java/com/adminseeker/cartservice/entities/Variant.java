package com.adminseeker.cartservice.entities;

import lombok.Data;

@Data
public class Variant {
    private Long variantId;
    private String variantSkucode;
    private String type;
    private String color;
    private String size;
    private Double price;
}
