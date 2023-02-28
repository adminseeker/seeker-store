package com.adminseeker.orderservice.proxies;

import java.util.List;

import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String name;
    private List<Address> addressList;
}
