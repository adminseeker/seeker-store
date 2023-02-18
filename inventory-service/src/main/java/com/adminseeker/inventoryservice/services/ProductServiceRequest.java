package com.adminseeker.inventoryservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.inventoryservice.proxies.ProductResponse;

@FeignClient(value="product-feign-client",url="http://localhost:8082/api/v1/products")
public interface ProductServiceRequest {

    @GetMapping("/skucode/{skucode}")
    Optional<ProductResponse> getProductBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/{productId}")
    Optional<ProductResponse> getProductById(@PathVariable("productId") Long productId);
}