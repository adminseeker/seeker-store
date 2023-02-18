package com.adminseeker.inventoryservice.entities;

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
    @Column(name="variant_id")
    private Integer variantId;

    @Column(name="variant_skucode")
    @NotEmpty(message = "Empty value not allowed!")
    private String variantSkucode;

    @Column(name="quantity")
    @NotNull(message = "Empty value not allowed!")
    private Integer quantity;

}
