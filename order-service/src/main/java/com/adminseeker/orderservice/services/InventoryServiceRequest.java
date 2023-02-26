package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.adminseeker.orderservice.proxies.QuantityResponse;

@FeignClient(value="inventory-feign-client",url="http://localhost:8083/api/v1/inventory")
public interface InventoryServiceRequest {
    
    @GetMapping("/skucode/{skucode}/quantity")
    Optional<QuantityResponse> getProductQuantityBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/skucode/{skucode}/variant/{variantSkucode}/quantity")
    Optional<QuantityResponse> getVariantQuantityBySkucode(@PathVariable("skucode") String skucode,@PathVariable("variantSkucode") String variantSkucode);

    @PutMapping("/skucode/{skucode}/quantity")
    Optional<QuantityResponse> updateProductQuantityBySkucode(@PathVariable("skucode") String skucode);

    @PutMapping("/skucode/{skucode}/variant/{variantSkucode}/quantity")
    Optional<QuantityResponse> updateVariantQuantityBySkucode(@PathVariable("skucode") String skucode,@PathVariable("variantSkucode") String variantSkucode);
    

}
