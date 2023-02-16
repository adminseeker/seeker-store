package com.adminseeker.productservice.proxies;

import lombok.Data;

@Data
public class User {
    private Long userId;
    private String name;
    private String role;
}
