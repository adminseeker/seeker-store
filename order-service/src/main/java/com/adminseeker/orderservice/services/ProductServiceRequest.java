package com.adminseeker.orderservice.services;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.adminseeker.orderservice.proxies.ProductResponse;

@FeignClient(value="product-feign-client",url="http://${PRODUCT_SERVICE_URL}/api/v1/products")
public interface ProductServiceRequest {

    @GetMapping("/skucode/{skucode}")
    Optional<ProductResponse> getProductBySkucode(@PathVariable("skucode") String skucode);

    @GetMapping("/{productId}")
    Optional<ProductResponse> getProductById(@PathVariable("productId") Long productId);
}