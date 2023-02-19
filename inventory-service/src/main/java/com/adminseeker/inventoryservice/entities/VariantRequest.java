package com.adminseeker.inventoryservice.entities;

import lombok.Data;

@Data
public class VariantRequest {
    Long userId;
    Variant variant;
}
