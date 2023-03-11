package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.orderservice.proxies.ProductResponse;

@FeignClient("productservice")
public interface ProductServiceRequest {

    @GetMapping("/api/v1/products/skucode/{skucode}")
    Optional<ProductResponse> getProductBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/api/v1/products/{productId}")
    Optional<ProductResponse> getProductById(@PathVariable("productId") Long productId);
}