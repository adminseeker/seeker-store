package com.adminseeker.userservice.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name="t_users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(name="name")
    @NotEmpty(message = "Empty value not allowed!")
    private String name;

    @Column(name="email")
    @Email(message = "Please Enter Valid Email!")
    @NotEmpty(message = "Empty value not allowed!")
    private String email;

    @Column(name="password")
    @NotEmpty(message = "Empty value not allowed!")
    private String password;

    @Column(name="phone")
    @NotEmpty(message = "Empty value not allowed!")
    @Pattern(regexp="(^$|[0-9]{10})")
    private String phone;
    
    @Column(name="role")
    @NotEmpty(message = "Empty value not allowed!")
    private String role;

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Address> addressList;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

}
