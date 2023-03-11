package com.adminseeker.orderservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.adminseeker.orderservice.proxies.QuantityUpdate;
import com.adminseeker.orderservice.proxies.QuantityUpdateRequest;

@FeignClient("inventoryservice")
public interface InventoryServiceRequest {
    
    @PostMapping("/api/v1/inventory/skucode/getquantity")
    Optional<List<QuantityUpdate>> getProductQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PostMapping("/api/v1/inventory/variant/skucode/getquantity")
    Optional<List<QuantityUpdate>> getVariantQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/api/v1/inventory/skucode/quantity")
    Optional<List<QuantityUpdate>> updateProductQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/api/v1/inventory/variant/skucode/quantity")
    Optional<List<QuantityUpdate>> updateVariantQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);
    
}
