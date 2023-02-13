package com.adminseeker.userservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
@Entity
@Table(name="t_address_list")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="address_id")
    private Long addressId;

    @Column(name="nickname")
    @NotEmpty(message = "Empty value not allowed!")
    private String nickname;

    @Column(name="phone")
    @NotEmpty(message = "Empty value not allowed!")
    @Pattern(regexp="(^$|[0-9]{10})")
    private String phone;
    
    @Column(name="country")
    @NotEmpty(message = "Empty value not allowed!")
    private String country;

    @Column(name="state")
    @NotEmpty(message = "Empty value not allowed!")
    private String state;

    @Column(name="city")
    @NotEmpty(message = "Empty value not allowed!")
    private String city;

    @Column(name="street")
    @NotEmpty(message = "Empty value not allowed!")
    private String street;

    @Column(name="postalcode")
    @NotEmpty(message = "Empty value not allowed!")
    private String postalCode;

}
