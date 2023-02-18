package com.adminseeker.inventoryservice.entities;

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
@Table(name="t_inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="inventory_id")
    private Long inventoryId;

    @Column(name="skucode")
    @NotEmpty(message = "Empty value not allowed!")
    private String skucode;

    @Column(name="quantity")
    @NotNull(message = "Empty value not allowed!")
    private Integer quantity;
    
    @Column(name="seller_id")
    @NotNull(message = "Empty value not allowed!")
    private Long sellerId;

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Variant> variants;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime modifiedDate;

}
