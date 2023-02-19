package com.adminseeker.productservice.entities;

import lombok.Data;

@Data
public class VariantRequest {
    Long userId;
    Variant variant;
}
