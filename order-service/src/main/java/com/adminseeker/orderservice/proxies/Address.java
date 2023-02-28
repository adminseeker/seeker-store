package com.adminseeker.orderservice.proxies;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class Address {

    private Long addressId;
    private String nickname;
    private String phone;
    private String country;
    private String state;
    private String city;
    private String street;
    private String postalCode;

}
