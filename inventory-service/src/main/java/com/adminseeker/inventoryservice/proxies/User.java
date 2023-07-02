package com.adminseeker.inventoryservice.proxies;

import lombok.Data;

@Data
public class User {
    private Long userId;
    private String name;
    private String role;
    private String email;
}
