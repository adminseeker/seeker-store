package com.adminseeker.inventoryservice.entities;

import java.util.List;

import lombok.Data;

@Data
public class VariantQuantityRequest {
    List<VariantQuantity> quantityUpdates;
}
