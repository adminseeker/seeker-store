package com.adminseeker.cartservice.entities;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "cart")
public class Cart {
    
    @Id
    private String cartId;
    @NotNull(message="Empty Value Not Allowed!")
    @Field(value = "userId")
    private Long userId;
    @Field(value = "items")
    private List<Item> items;
}
