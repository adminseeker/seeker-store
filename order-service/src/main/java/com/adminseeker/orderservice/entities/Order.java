package com.adminseeker.orderservice.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Order {
    
    @Id
    private String orderId;

    @NotEmpty(message = "Empty Value Not Allowed!")
    private List<Item> items;

    @NotNull(message="Empty Value Not Allowed!")
    private Long userId;

    @NotEmpty(message = "Empty Value Not Allowed!")
    private String status;

    @NotNull(message="Empty Value Not Allowed!")
    private Long addressId;

    @NotNull(message="Empty Value Not Allowed!")
    private Double totalPrice;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
