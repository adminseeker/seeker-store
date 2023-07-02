package com.adminseeker.orderservice.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.orderservice.proxies.QuantityUpdate;
import com.adminseeker.orderservice.proxies.QuantityUpdateRequest;

@FeignClient("inventoryservice")
public interface InventoryServiceRequest {
    
    @PostMapping("/api/v1/inventory/public/skucode/getquantity")
    Optional<List<QuantityUpdate>> getProductQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PostMapping("/api/v1/inventory/public/variant/skucode/getquantity")
    Optional<List<QuantityUpdate>> getVariantQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/api/v1/inventory/inapi/skucode/quantity")
    Optional<List<QuantityUpdate>> updateProductQuantityBySkucodes(@RequestHeader Map<String,String> headers,@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/api/v1/inventory/inapi/variant/skucode/quantity")
    Optional<List<QuantityUpdate>> updateVariantQuantityBySkucodes(@RequestHeader Map<String,String> headers,@RequestBody QuantityUpdateRequest quantityUpdateRequest);
    
}
