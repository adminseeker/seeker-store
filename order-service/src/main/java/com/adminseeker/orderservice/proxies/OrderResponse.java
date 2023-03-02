package com.adminseeker.orderservice.proxies;

import java.util.List;

import lombok.Data;

@Data
public class OrderResponse {
    private String orderId;
    private Long userId;
    private String status;
    private Address address;
    private Double totalPrice;
    private List<OrderItemResponse> items;
}
