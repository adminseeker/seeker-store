package com.adminseeker.productservice.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subCategories = new ArrayList<>();

    public void addSubcategory(Category subcategory) {
        subCategories.add(subcategory);
        subcategory.setParentCategory(this);
    }

    public void removeSubcategory(Category subcategory) {
        subCategories.remove(subcategory);
        subcategory.setParentCategory(null);
    }
}