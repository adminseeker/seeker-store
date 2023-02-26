package com.adminseeker.orderservice.entities;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document
public class Item {
    
    @Id
    private String itemId;
    @NotNull(message="Empty Value Not Allowed!")
    @Field(value = "productId")
    private Long productId;
    @Field(value = "variantId")
    private Long variantId;
    @NotNull(message="Empty Value Not Allowed!")
    @Field(value = "quantity")
    private Integer quantity;
    @NotNull(message="Empty Value Not Allowed!")
    @Field(value = "price")
    private Double price;

}
