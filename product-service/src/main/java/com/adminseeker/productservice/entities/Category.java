package com.adminseeker.productservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
@Entity
@Table(name="t_categories")
public class Category {

    @Id
    @Column(name="category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(name="category_name")
    @NotEmpty(message = "Empty value not allowed!")
    private String categoryName;

    @Column(name="category_code")
    @NotEmpty(message = "Empty value not allowed!")
    private String categoryCode;

    @Column(name="subCategory")
    private Category subCategory;
}
