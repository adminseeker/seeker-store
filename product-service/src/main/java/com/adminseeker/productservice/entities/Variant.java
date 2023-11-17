package com.adminseeker.productservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
@Table(name="t_variants_list")
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="variant_id")
    private Long variantId;

    @Column(name="variant_skucode")
    @NotEmpty(message = "Empty value not allowed!")
    private String variantSkucode;

    @Column(name="category_name")
    @NotEmpty(message = "Empty value not allowed!")
    private String categoryName;

    @Column(name="category_code")
    @NotEmpty(message = "Empty value not allowed!")
    private String categoryCode;

    @Column(name="image_path")
    @NotEmpty(message = "Empty value not allowed!")
    private String imagePath;

    @Column(name="description")
    @NotEmpty(message = "Empty value not allowed!")
    private String description;

    @Column(name="color")
    private String color;

    @Column(name="type")
    private String type;

    @Column(name="size")
    private String size;

    @Column(name="price")
    @NotNull(message = "Empty value not allowed!")
    private Double price;
    

}
