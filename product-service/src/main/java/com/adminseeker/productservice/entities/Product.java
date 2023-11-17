package com.adminseeker.productservice.entities;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name="t_products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long productId;

    @Column(name="name")
    @NotEmpty(message = "Empty value not allowed!")
    private String name;

    @Column(name="description")
    @NotEmpty(message = "Empty value not allowed!")
    private String description;

    @Column(name="skucode")
    @NotEmpty(message = "Empty value not allowed!")
    private String skucode;

    @Column(name="image_path")
    @NotEmpty(message = "Empty value not allowed!")
    private String imagePath;

    @Column(name="price")
    @NotNull(message = "Empty value not allowed!")
    private Double price;
    
    @Column(name="seller_id")
    @NotNull(message = "Empty value not allowed!")
    private Long sellerId;

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Variant> variants;

    private List<String> categoryCodes;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

}
