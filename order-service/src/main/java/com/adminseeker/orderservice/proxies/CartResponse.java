package com.adminseeker.orderservice.proxies;

import java.util.List;


import lombok.Data;

@Data
public class CartResponse {
    private String cartId;
    private Long userId;
    private List<ItemResponse> itemsResponse;
    private Double totalPrice;
}
