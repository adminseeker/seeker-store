package com.adminseeker.orderservice.proxies;

import java.util.List;

import lombok.Data;

@Data
public class QuantityUpdateRequest {
    List<QuantityUpdate> quantityUpdates;
}
