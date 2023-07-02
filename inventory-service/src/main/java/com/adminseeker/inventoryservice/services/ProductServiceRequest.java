package com.adminseeker.inventoryservice.services;

import java.util.Map;
import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.adminseeker.inventoryservice.proxies.ProductResponse;

@FeignClient("productservice")
public interface ProductServiceRequest {

    @GetMapping("/api/v1/products/public/skucode/{skucode}")
    Optional<ProductResponse> getProductBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/api/v1/products/public/{productId}")
    Optional<ProductResponse> getProductById(@PathVariable("productId") Long productId, @RequestHeader Map<String,String> headers);
}