package com.adminseeker.orderservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.adminseeker.orderservice.proxies.QuantityUpdate;
import com.adminseeker.orderservice.proxies.QuantityUpdateRequest;

@FeignClient(value="inventory-feign-client",url="http://${INVENTORY_SERVICE_URL:localhost:8083}/api/v1/inventory")
public interface InventoryServiceRequest {
    
    @PostMapping("/skucode/getquantity")
    Optional<List<QuantityUpdate>> getProductQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PostMapping("/variant/skucode/getquantity")
    Optional<List<QuantityUpdate>> getVariantQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/skucode/quantity")
    Optional<List<QuantityUpdate>> updateProductQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);

    @PutMapping("/variant/skucode/quantity")
    Optional<List<QuantityUpdate>> updateVariantQuantityBySkucodes(@RequestBody QuantityUpdateRequest quantityUpdateRequest);
    
}
