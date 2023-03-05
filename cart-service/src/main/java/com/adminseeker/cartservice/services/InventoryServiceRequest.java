package com.adminseeker.cartservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.cartservice.proxies.QuantityResponse;

@FeignClient(value="inventory-feign-client",url="http://${INVENTORY_SERVICE_URL}/api/v1/inventory")
public interface InventoryServiceRequest {
    
    @GetMapping("/skucode/{skucode}/quantity")
    Optional<QuantityResponse> getProductQuantityBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/skucode/{skucode}/variant/{variantSkucode}/quantity")
    Optional<QuantityResponse> getVariantQuantityBySkucode(@PathVariable("skucode") String skucode,@PathVariable("variantSkucode") String variantSkucode);
}
