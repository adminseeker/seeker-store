package com.adminseeker.productservice.entities;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import lombok.Data;

@Data
public class ProductRequest {

    @NotEmpty(message = "Empty value not allowed!")
    private String name;

    @NotEmpty(message = "Empty value not allowed!")
    private String description;

    @NotEmpty(message = "Empty value not allowed!")
    private String skucode;

    @NotEmpty(message = "Empty value not allowed!")
    private String imagePath;

    @NotNull(message = "Empty value not allowed!")
    private Double price;
    
    @NotNull(message = "Empty value not allowed!")
    private Long sellerId;
    
    @NotNull(message = "Empty value not allowed!")
    private Long categoryId;

}
